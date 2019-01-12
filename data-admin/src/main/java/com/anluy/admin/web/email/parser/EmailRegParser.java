package com.anluy.admin.web.email.parser;

import com.alibaba.fastjson.JSON;
import com.anluy.admin.entity.CftRegInfo;
import com.anluy.admin.entity.EmailIp;
import com.anluy.admin.entity.EmailReg;
import com.anluy.admin.utils.FileUtils;
import com.anluy.admin.utils.IPAddrUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能说明：email邮箱注册信息文件解析
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class EmailRegParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailRegParser.class);
    private final String fileId;
    private IPAddrUtil ipAddrUtil;
    public EmailRegParser(String fileId, IPAddrUtil ipAddrUtil) {
        this.fileId = fileId;
        this.ipAddrUtil = ipAddrUtil;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<EmailReg> parser(File file) throws Exception {
        String charset = FileUtils.detectorCharset(file);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
        String line = null;
        List<String> stringList = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            stringList.add(line);
        }
        return parse(stringList);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<EmailReg> parser(String path) throws Exception {
        return parser(new File(path));
    }

    private List<EmailReg> parse(List<String> txtContent) throws ParseException {
        if (txtContent.size() < 2) {
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        String emailType = null;
        for (int i = 0; i < txtContent.size(); i++) {
            String line = txtContent.get(i);
            if (line.startsWith("用户名、唯一号")) {
                emailType = "sina";
                break;
            } else if (line.startsWith("查询用户:") && line.indexOf("163.com") > 5) {
                emailType = "163";
                break;
            }
        }

        switch (emailType){
            case "sina":
                return parserSina(txtContent);
            case "163":
                return parser163(txtContent);
        }
        return null;
    }

    /**
     * 解析新浪邮箱注册信息
     *
     * @param txtContent
     */
    private List<EmailReg> parserSina(List<String> txtContent) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<EmailReg> resultList = new ArrayList<>();
        EmailReg dataMap = null;
        boolean startIp = false;
        StringBuffer txtData = null;
        for (int i = 0; i < txtContent.size(); i++) {
            String line = txtContent.get(i);
            if (line.startsWith("用户名、唯一号")) {
                txtData = new StringBuffer();
                startIp = false;
                dataMap = new EmailReg();
                dataMap.setType("新浪");
                dataMap.setTxtData(txtData);
                dataMap.setEmail(line.split("：|:")[1]);
                resultList.add(dataMap);
            } else if (line.startsWith("手机")) {
                String[] strs = line.split("：|:");
                dataMap.setSjhm(strs.length==2?strs[1]:null);
                dataMap.setDhhm(null);
            }else if (line.startsWith("时间\tIP")) {
                startIp = true;
            }else if(startIp && StringUtils.isBlank(line)){
                txtData = null;
                startIp = false;
                continue;
            }else if(startIp){
                String[] ipstrs =  line.split("\t");
                if(ipstrs.length==2){
                    EmailIp ipmap = new EmailIp();
                    ipmap.setTime(sdf.parse(ipstrs[0].replace("/","-")));
                    ipmap.setIp(ipstrs[1]);
                    ipmap.setGsd(ipAddrUtil.findCityInfoString(ipmap.getIp()));
                    ipmap.setEmail(dataMap.getEmail());
                    ipmap.setType(dataMap.getType());
                    dataMap.addIplist(ipmap);
                }
            }
            if(txtData!=null){
                txtData.append(line).append("\n");
            }
        }
        //System.out.println(JSON.toJSONString(resultList));
        return resultList;
    }

    /**
     * 解析163邮箱注册信息
     *
     * @param txtContent
     */
    private List<EmailReg> parser163(List<String> txtContent) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<EmailReg> resultList = new ArrayList<>();
        EmailReg dataMap = null;
        boolean startIp = false;
        StringBuffer txtData = null;
        for (int i = 0; i < txtContent.size(); i++) {
            String line = txtContent.get(i);
            if (line.startsWith("查询用户")) {
                txtData = new StringBuffer();
                startIp = false;
                dataMap = new EmailReg();
                dataMap.setType("网易163");
                dataMap.setTxtData(txtData);
                dataMap.setEmail( line.split("：|:")[1]);
                resultList.add(dataMap);
            } else if (line.startsWith("电话号码")) {
                String[] strs = line.split("：|:");
                dataMap.setDhhm(strs.length==2?strs[1]:null);
            }else if (line.startsWith("手机号码")) {
                String[] strs = line.split("：|:");
                dataMap.setSjhm( strs.length==2?strs[1]:null);
            }else if (line.indexOf("Webmail登录记录")>5) {
                startIp = true;
            }else if(startIp && StringUtils.isBlank(line)){
                txtData = null;
                startIp = false;
                continue;
            }else if(startIp){
                String[] ipstrs =  line.split("\t");
                if(ipstrs.length==2){
                    EmailIp ipmap = new EmailIp();
                    ipmap.setTime(sdf.parse(ipstrs[0]));
                    ipmap.setIp(ipstrs[1]);
                    ipmap.setEmail(dataMap.getEmail());
                    ipmap.setType(dataMap.getType());
                    dataMap.addIplist(ipmap);
                }
            }
            if(txtData!=null){
                txtData.append(line).append("\n");
            }
        }
        //System.out.println(JSON.toJSONString(resultList));
        return resultList;
    }
//
//    public static void main(String[] args) {
//        EmailRegParser parser = new EmailRegParser("");
//        try {
//            Object resultList = parser.parser("H:\\数据管理系统\\数据导入20180629\\colin8787@163.com 查询结果.txt");
//            System.out.println(JSON.toJSONString(resultList));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
