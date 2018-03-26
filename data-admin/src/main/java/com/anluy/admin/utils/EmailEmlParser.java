package com.anluy.admin.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.anluy.admin.entity.Email;
import com.sun.mail.util.BASE64DecoderStream;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.*;
import sun.misc.BASE64Encoder;


/**
 * 功能说明：email eml 文件解析类
 * <p>
 * Created by hc.zeng on 2018/3/14.
 */
public class EmailEmlParser {
    private final String fileDir;
    private final String emailId;

    public EmailEmlParser(String fileDir, String emailId) {
        this.fileDir = fileDir;
        this.emailId = emailId;
    }
//    public static void main(String[] args) {
//        EmailEmlParser parser = new EmailEmlParser();
//        try {
//            Email email = parser.parser("C:\\Users\\Administrator\\Desktop\\数据管理系统\\现在开始试用Wijmo Enterprise产品-.eml");
//            System.out.println(JSON.toJSONString(email));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 解析邮件eml
     *
     * @param emlFile
     * @return
     * @throws Exception
     */
    public Email parser(File emlFile) throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        InputStream inMsg = null;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(emlFile));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            break;
        }

        //xml包装过的，先解析
        if (line.startsWith("<?xml")) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //创建DocumentBuilder对象
            DocumentBuilder db = dbf.newDocumentBuilder();
            //通过DocumentBuilder对象的parser方法加载books.xml文件到当前项目下
            Document document = db.parse(emlFile);
            //获取所有book节点的集合
            NodeList responseList = document.getElementsByTagName("Response");
            Element respone = (Element) responseList.item(0);
            NodeList resultList = respone.getElementsByTagName("result");
            NodeList contentList = ((Element) resultList.item(0)).getElementsByTagName("content");
            Node contentNode = contentList.item(0).getFirstChild();
            if (contentNode.getNodeType() == Node.CDATA_SECTION_NODE) {
                CDATASection cdataNode = (CDATASection) contentNode;
                String sub = cdataNode.getTextContent();
                inMsg = new ByteArrayInputStream(sub.getBytes());
            }
        } else {
            inMsg = new FileInputStream(emlFile);
        }

        Message msg = new MimeMessage(session, inMsg);
        return parseEml(msg);
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
    private Email parseEml(Message msg) throws Exception {
        //邮件对象
        Email email = new Email();
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
        if (o instanceof Multipart) {
            Multipart multipart = (Multipart) o;
            reMultipart(email, multipart);
        } else if (o instanceof Part) {
            Part part = (Part) o;
            rePart(email, part);
        } else if (o instanceof BodyPart) {
            reBodyPart(email, (BodyPart) o);
        } else {
            email.addContent(msg.getContent());//内容
        }
        return email;
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
            String fp = "/" + emailId + "/" + strFileNmae;
            if (!StringUtils.isEmpty(strFileNmae)) {    // MimeUtility.decodeText解决附件名乱码问题
                strFileNmae = MimeUtility.decodeText(strFileNmae);
                System.out.println("发现附件: " + strFileNmae);
                InputStream in = part.getInputStream();// 打开附件的输入流
                // 读取附件字节并存储到文件中
                File ff = new File(fileDir + "/" + emailId);
                if (!ff.exists() && !ff.isDirectory()) {
                    ff.mkdirs();
                }

                java.io.FileOutputStream out = new FileOutputStream(fileDir + fp);
                int data;
                while ((data = in.read()) != -1) {
                    out.write(data);
                }
                in.close();
                out.close();
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
        if (bodyPart.getDisposition() != null) {
            String strFileNmae = bodyPart.getFileName();
            String fp = "/" + emailId + "/" + strFileNmae;
            if (!StringUtils.isEmpty(strFileNmae)) {    // MimeUtility.decodeText解决附件名乱码问题
                strFileNmae = MimeUtility.decodeText(strFileNmae);
                System.out.println("发现附件: " + strFileNmae);
                InputStream in = bodyPart.getInputStream();// 打开附件的输入流
                // 读取附件字节并存储到文件中
                File ff = new File(fileDir + "/" + emailId);
                if (!ff.exists() && !ff.isDirectory()) {
                    ff.mkdirs();
                }

                java.io.FileOutputStream out = new FileOutputStream(fileDir + fp);
                int data;
                while ((data = in.read()) != -1) {
                    out.write(data);
                }
                in.close();
                out.close();
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
}
