package com.anluy.admin.web.email.parser;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.alibaba.fastjson.JSON;
import com.anluy.admin.entity.Email;
import com.anluy.admin.utils.FileUtils;
import com.sun.mail.util.BASE64DecoderStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;


/**
 * 功能说明：email eml 文件解析类
 * <p>
 * Created by hc.zeng on 2018/3/14.
 */
public class EmailEmlParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailEmlParser.class);
    private static ExecutorService executor = Executors.newScheduledThreadPool(5);
    private final String fileDir;
    private final String emailId;
    int ii = 0x17;
    public EmailEmlParser(String fileDir, String emailId) {
        this.fileDir = fileDir;
        this.emailId = emailId;
    }

    /**
     * 解析邮件eml
     *
     * @param emlFile
     * @return
     * @throws Exception
     */
    public Email parser(File emlFile) throws IOException {
        InputStream inMsg = null;
        try{
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            String charset = FileUtils.detectorCharset(emlFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(emlFile), charset));
            //BufferedReader bufferedReader = new BufferedReader(new FileReader(emlFile));
            String line = null;
            StringBuffer sbl = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                sbl.append(line).append("\n");
                //System.out.println(line);
            }
            bufferedReader.close();
            inMsg =  new FileInputStream(emlFile);
            String content = null;
            //xml包装过的，先解析﻿<?xml version="1.0" encoding="UTF-8"?>
            if (sbl.indexOf("<?xml") >= 0) {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                //创建DocumentBuilder对象
                DocumentBuilder db = dbf.newDocumentBuilder();
                //先按照xml解析出content的内容
                //如果不能按照xml解析说明文件里有编码问题，
                try{
                    Document document = db.parse(inMsg);
                    inMsg.close();
                    inMsg =  new FileInputStream(emlFile);
                    //获取所有book节点的集合
                    NodeList responseList = document.getElementsByTagName("Response");
                    Element respone = (Element) responseList.item(0);
                    NodeList resultList = respone.getElementsByTagName("result");
                    NodeList contentList = ((Element) resultList.item(0)).getElementsByTagName("content");
                    Node contentNode = contentList.item(0).getFirstChild();
                    if (contentNode.getNodeType() == Node.CDATA_SECTION_NODE) {
                        CDATASection cdataNode = (CDATASection) contentNode;
                        content = cdataNode.getTextContent();
                        inMsg = new ByteArrayInputStream(content.getBytes("gbk"));

                        String[] sbu = content.split("\n");
                        StringBuffer sb = new StringBuffer();
                        boolean st = false;
                        for (String ll : sbu) {
                            if ("".equals(ll)) {
                                st = true;
                            }
                            if (st) {
                                sb.append(ll).append("\n");
                            }
                        }
                        content = sb.toString();

                    }
                }catch (Exception e){
                    // 不能解析，直接截取字符串，截取出content的内容
                    inMsg.close();
                    inMsg =  new FileInputStream(emlFile);
                    content = sbl.toString();
                    content = content.substring(content.indexOf("<content><![CDATA[")+18);
                    content = content.substring(0,content.indexOf("]]></content>"));
                    //System.out.println(content);
                }
            } else {
                //如果是纯文本格式的，直接读取
//                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(emlFile), charset));
//                StringBuffer sb = new StringBuffer();
//                line = null;
//                boolean st = false;
//                while ((line = bufferedReader.readLine()) != null) {
//                    if ("".equals(line)) {
//                        st = true;
//                    }
//                    if (st) {
//                        sb.append(line).append("\n");
//                    }
//                }
                content = sbl.toString();
            }

            Message msg = new MimeMessage(session, inMsg);
            return parseEml(msg, content);
        }catch (Exception e){
            e.printStackTrace();
            Email email = new Email();
            email.setSubject("邮件格式异常不能解析");
            email.addContent("<h1>邮件格式异常不能解析</h1>");
            return email;
        }finally {
            if(inMsg!=null){
                inMsg.close();
            }
        }
    }

    /**
     * 解析邮件eml
     *
     * @param emlPath
     * @return
     * @throws Exception
     */
    public Email parser(String emlPath) throws Exception {
        return parser(new File(emlPath));
    }

    /**
     * @param msg
     * @throws Exception
     */
    private Email parseEml(Message msg, String content) {
        //邮件对象
        Email email = new Email();
        try{
            // 发件人信息
            Address[] froms = msg.getFrom();
            if (froms != null) {
                InternetAddress addr = (InternetAddress) froms[0];
                email.setFrom(addr.getPersonal());
                email.setFromAddress(addr.getAddress());
            }
            Address[] tos = msg.getRecipients(Message.RecipientType.TO);
            if (tos != null) {
                InternetAddress addr = (InternetAddress) tos[0];
                email.setTo(addr.getPersonal());
                email.setToAddress(addr.getAddress());
            }
            //邮件主题
            email.setSubject(msg.getSubject());
            //接收时间
            email.setReceivedDate(msg.getSentDate());

            // getContent() 是获取包裹内容, Part相当于外包装
            Object o = msg.getContent();
            //System.out.println(msg.getContentType());
            if (o instanceof Multipart) {
                Multipart multipart = (Multipart) o;
                reMultipart(email, multipart);
            } else if (o instanceof Part) {
                Part part = (Part) o;
                rePart(email, part);
            } else if (o instanceof BodyPart) {
                reBodyPart(email, (BodyPart) o);
            } else if (o instanceof BASE64DecoderStream) {
                InputStream is = (InputStream) o;
                int data = 0;
                List<Byte> l = new ArrayList<>();
                try {
                    data = is.read();
                    while (data != -1) {
                        l.add((byte) data);
                        data = is.read();
                    }
                    byte[] bb = new byte[l.size()];
                    for (int i = 0; i < l.size(); i++) {
                        bb[i] = l.get(i);
                    }
                    email.addContent("<h1>加密邮件，不能解析</h1>"+ new String(bb));//内容
                } catch (Exception e) {
                    parseMy(email, content);
                } finally {
                    is.close();
                }
            } else if (o instanceof InputStream) {
                InputStream is = (InputStream) o;
                int data = 0;
                List<Byte> l = new ArrayList<>();
                do {
                    try {
                        data = is.read();
                        //System.out.println(data);
                        if (data != -1)
                            l.add((byte) data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (data != -1);
                byte[] bb = new byte[l.size()];
                for (int i = 0; i < l.size(); i++) {
                    bb[i] = l.get(i);
                }
                String str = new String(bb);
                parseMy(email, str);
                is.close();
            } else {
                email.addContent(msg.getContent());//内容
            }

        }catch (Exception e){
            if(StringUtils.isNotBlank(content)){
                try {
                    parseMy(email, content);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    if(StringUtils.isBlank(email.getSubject())){
                        email.setSubject("邮件格式异常不能解析");
                    }
                    email.addContent("<h1>邮件格式异常不能解析</h1>");
                }
            }
        }
        return email;
    }


    /**
     * 自定义解析
     *
     * @param content
     * @return
     */
    private void parseMy(Email email, String content) throws Exception {
        String[] contentStr = content.split("\n");
        Object ob = parse(email,contentStr);
    }

    private static final String BOUNDARY = "------=";
    private static final String ENCODING = "Content-Transfer-Encoding";

    /**
     * 自定义解析
     *
     * @param contentStr
     * @return
     */
    private Object parse(Email email,String[] contentStr) throws IOException {
        int boundary = -1;
        String boundaryStr = null;
        List<List<String>> list = new ArrayList<>();
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < contentStr.length; i++) {
            String line = contentStr[i].trim();
            if (boundaryStr != null && line.startsWith(boundaryStr)) {
                list.add(lineList);
                lineList = new ArrayList<>();
            } else if (line.startsWith(BOUNDARY)) {
                boundary = i;
                boundaryStr = line;
            } else if (boundary > -1) {
                if(lineList.size()>0 && StringUtils.isBlank(lineList.get(0))){
                    lineList.remove(0);
                }
                lineList.add(line);
            }
        }
        //如果不能往下分组了
        if(list.isEmpty()){
            String encoding = null;
            String name = null;
            String filename = null;
            String charset = null;
            boolean filepart = false;
            boolean startRed = false;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < contentStr.length; i++) {
                String line = contentStr[i].trim();
                if(line.startsWith("Content-Type")){
                    String[] spl = line.split(";");
                    if(spl.length>=1){
                        String ct = spl[0];
                        String[] cts = ct.split(":");
                        if(cts.length>1 && cts[1].trim().startsWith("application/octet-stream")){
                            filepart = true;
                        }
                    }
                }else if(line.startsWith(ENCODING)){
                    encoding = line.split(":")[1].trim();
                }else if(line.startsWith("name")){
                    name = line.split("=")[1].trim().replace("\"","");
                }else if(line.startsWith("charset")){
                    charset = line.split("=")[1].trim().replace("\"","");
                }else if(line.startsWith("filename")){
                    filename = line.split("=")[1].trim().replace("\"","");
//                }else if(line.startsWith("Content-Disposition")){
//                    ;
                }else if(StringUtils.isBlank(line)){
                    startRed = true;
                }else if(startRed){
                    sb.append(line).append("\n");
                }
            }
            if(filepart){
                //System.out.println(sb.toString());
                Base64.Decoder decoder = Base64.getDecoder();
                byte[] bb = decoder.decode(sb.toString());
                for(int i=0;i<bb.length;++i){
                    if(bb[i]<0){//调整异常数据
                        bb[i]+=256;
                    }
                }
                String strFileNmae = filename.replaceAll("\\|/|\\:|\\*|\\?|\"|\\<|\\>|\\|", "");
                String fp = "/" + emailId + "/" + strFileNmae;
                System.out.println("发现附件: " + strFileNmae);
                File ff = new File(fileDir + "/" + emailId);
                if (!ff.exists() && !ff.isDirectory()) {
                    ff.mkdirs();
                }
                executor.submit(new FileRunnable(null,new File(fileDir + fp),bb));
                email.addFile(fp, MimeUtility.decodeText("file"));//内容
            }else {
                if("base64".equals(encoding)){
                    Base64.Decoder decoder = Base64.getDecoder();
                    byte[] bb = decoder.decode(sb.toString());
                    for(int i=0;i<bb.length;++i){
                        if(bb[i]<0){//调整异常数据
                            bb[i]+=256;
                        }
                    }
                    String str = new String(bb,charset==null?"GBK":charset);
                    email.addContent(str);
                }else {
                    email.addContent(sb.toString());
                }
            }

        }else {
            for (List<String> sl : list) {
                String[] strs = new String[sl.size()];
                sl.toArray(strs);
                parse(email,strs);
            }
        }
        return list;
    }

    /**
     * 读取邮件单一部分
     *
     * @param email 邮件对象
     * @param part  解析内容
     * @throws Exception
     */
    private void rePart(Email email, Part part) throws Exception {

        if (part.getDisposition() != null) {

            String strFileNmae = part.getFileName();
            if (!StringUtils.isEmpty(strFileNmae)) {    // MimeUtility.decodeText解决附件名乱码问题
                strFileNmae = MimeUtility.decodeText(strFileNmae);
                System.out.println("发现附件: " + strFileNmae);
                strFileNmae = strFileNmae.replaceAll("\\|/|\\:|\\*|\\?|\"|\\<|\\>|\\|", "");
                String fp = "/" + emailId + "/" + strFileNmae;
                InputStream in = part.getInputStream();// 打开附件的输入流
                // 读取附件字节并存储到文件中
                File ff = new File(fileDir + "/" + emailId);
                if (!ff.exists() && !ff.isDirectory()) {
                    ff.mkdirs();
                }
                executor.submit(new FileRunnable(in,new File(fileDir + fp),null));
                email.addFile(fp, MimeUtility.decodeText(part.getContentType()));//内容
            }
        } else {
            email.addContent(part.getContent());//内容
        }
    }

    /**
     * 读取多个邮件包裹
     *
     * @param email     邮件对象
     * @param multipart // 接卸包裹（含所有邮件内容(包裹+正文+附件)）
     * @throws Exception
     */
    private void reMultipart(Email email, Multipart multipart) throws Exception {
        // System.out.println("邮件共有" + multipart.getCount() + "部分组成");
        // 依次处理各个部分
        for (int j = 0, n = multipart.getCount(); j < n; j++) {
            // System.out.println("处理第" + j + "部分");
            Part part = multipart.getBodyPart(j);// 解包, 取出 MultiPart的各个部分,
            // 每部分可能是邮件内容,
            // 也可能是另一个小包裹(MultipPart)
            // 判断此包裹内容是不是一个小包裹, 一般这一部分是 正文 Content-Type: multipart/alternative
            if (part.getContent() instanceof Multipart) {
                Multipart p = (Multipart) part.getContent();// 转成小包裹
                // 递归迭代
                reMultipart(email, p);
            } else if (part instanceof BodyPart) {
                reBodyPart(email, (BodyPart) part);
            } else {
                rePart(email, part);
            }
        }
    }

    /**
     * 读取多个邮件包裹
     *
     * @param email    邮件对象
     * @param bodyPart //
     * @throws Exception
     */
    private void reBodyPart(Email email, BodyPart bodyPart) throws Exception {
        if (bodyPart.getDisposition() != null || bodyPart instanceof MimeBodyPart) {
            String strFileNmae = bodyPart.getFileName();
            if (!StringUtils.isEmpty(strFileNmae)) {
                // MimeUtility.decodeText解决附件名乱码问题
                strFileNmae = MimeUtility.decodeText(strFileNmae);
                strFileNmae = strFileNmae.replaceAll("\\|/|\\:|\\*|\\?|\"|\\<|\\>|\\|", "");
                String fp = "/" + emailId + "/" + strFileNmae;
                System.out.println("发现附件: " + strFileNmae);
                InputStream in = bodyPart.getInputStream();// 打开附件的输入流
                // 读取附件字节并存储到文件中
                File ff = new File(fileDir + "/" + emailId);
                if (!ff.exists() && !ff.isDirectory()) {
                    ff.mkdirs();
                }
                executor.submit(new FileRunnable(in,new File(fileDir + fp),null));
                email.addFile(fp, MimeUtility.decodeText(bodyPart.getContentType()));//内容
            } else {
                if (MimeUtility.decodeText(bodyPart.getContentType()).indexOf("text/html") >= 0) {
                    email.addContent(bodyPart.getContent());//内容
                }

            }
        } else {
            email.addContent(bodyPart.getContent());//内容
        }
    }

    private class FileRunnable implements Runnable{
        InputStream in ;
        File file ;
        byte[] bb ;

        public FileRunnable(InputStream in, File file,byte[] bb) {
            this.in = in;
            this.file = file;
            this.bb = bb;
        }

        @Override
        public void run() {
            try {
                if(in!=null){
                    int data;
                    java.io.FileOutputStream out = new FileOutputStream(file);
                    while ((data = in.read()) != -1) {
                        out.write(data);
                    }
                    in.close();
                    out.close();
                }else if(bb !=null){
                    java.io.FileOutputStream out = new FileOutputStream(file);
                    out.write(bb);
                    out.close();
                }

            } catch (IOException e) {
                LOGGER.error("本地存储email的附件失败！",e);
            }

        }
    }

    public static void main(String[] args) {
        EmailEmlParser parser = new EmailEmlParser("H:\\数据管理系统\\数据导入20180711\\", "");
        try {
            Email email =  parser.parser("H:\\数据管理系统\\导入数据20180601\\IM4356055160\\1302587787\\mail\\recvmail\\2017_12_15_10_45_56.eml");
            System.out.println(JSON.toJSONString(email));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
