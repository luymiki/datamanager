package com.anluy.admin.web.weixin.parser;

import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.*;
import com.anluy.admin.utils.IPAddrUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 功能说明：微信文件解析
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class WeiXinRegParser2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeiXinRegParser2.class);
    private WxregInfo regInfo = new WxregInfo();
    private IPAddrUtil ipAddrUtil;
    private FileManagerConfig fileManagerConfig;

    public WeiXinRegParser2(IPAddrUtil ipAddrUtil, FileManagerConfig fileManagerConfig) {
        this.ipAddrUtil = ipAddrUtil;
        this.fileManagerConfig = fileManagerConfig;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<String> read(File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = null;
        List<String> stringList = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            stringList.add(line);
        }
        return stringList;
    }

    /**
     * 解析文件
     *
     * @return
     * @throws Exception
     */
    public WxregInfo parser(List<Attachment> attachments) throws Exception {
        //找注册信息文件
        Attachment regAttachment = null;
        for (Attachment attachment : attachments) {
            if(attachment.getName().indexOf("RegInfo")>=0){
                regAttachment = attachment;
            }
        }
        if(regAttachment==null){
            return null;
        }
        List<String> txtContent = read(new File(fileManagerConfig.getUploadDir() + regAttachment.getPath()));
        parseReg(txtContent);
        regInfo.setFileId(regAttachment.getId());
        regInfo.setSuspId(regAttachment.getSuspId());
        regInfo.setSuspName(regAttachment.getSuspName());
        regInfo.setTags(regAttachment.getTags());
        for (Attachment attachment : attachments) {
            if(attachment.getName().indexOf("Friends")>=0){
                parseLxr(regAttachment, read(new File(fileManagerConfig.getUploadDir() + attachment.getPath())));
            }else if(attachment.getName().indexOf("Groups")>=0){
                parseQun(regAttachment, read(new File(fileManagerConfig.getUploadDir() + attachment.getPath())));
            }else if(attachment.getName().indexOf("登录轨迹")>=0 || attachment.getName().indexOf("ip")>=0 || attachment.getName().indexOf("login")>=0){
                parseLoginip(regAttachment, read(new File(fileManagerConfig.getUploadDir() + attachment.getPath())));
            }
        }
        return regInfo;
    }

    private void parseReg(List<String> txtContent) {
        for (int i = 0; i < txtContent.size(); i++) {
            String line = txtContent.get(i);
            if (StringUtils.isNotBlank(line)) {
                if(line.charAt(0)==65279){
                    line = line.substring(1,line.length());
                }
                String[] infos = line.replace("\t", "").split("：");
                if (infos.length == 2) {
                    switch (infos[0]) {
                        case "微信号": {
                            regInfo.setWeixin(infos[1]);
                            break;
                        }
                        case "QQ": {
                            if (infos[1] != null && infos[1].length() > 4) {
                                regInfo.setQq(infos[1]);
                            }
                            break;
                        }
                        case "手机号": {
                            regInfo.setDh(infos[1]);
                            break;
                        }
                        case "邮箱": {
                            regInfo.setEmail(infos[1]);
                            break;
                        }
                        case "别名": {
                            regInfo.setBm(infos[1]);
                            break;
                        }
                        case "昵称": {
                            regInfo.setName(infos[1]);
                            break;
                        }
                        case "签名": {
                            regInfo.setQm(infos[1]);
                            break;
                        }
                        case "性别": {
                            regInfo.setXb(infos[1]);
                            break;
                        }
                        case "省份": {
                            regInfo.setSf(infos[1]);
                            break;
                        }
                        case "城市": {
                            regInfo.setCs(infos[1]);
                            break;
                        }
                    }
                }

            }
        }
    }
    private void parseLxr(Attachment attachment, List<String> txtContent) {
        List<Wxlxr> wxlxrList = new ArrayList<>();
        regInfo.setWxlxrList(wxlxrList);
        for (int i = 0; i < txtContent.size(); i++) {
            String line = txtContent.get(i);
            if (StringUtils.isNotBlank(line)) {
                if(line.indexOf("联系人")>=0 || line.indexOf("帐号\t别名")>=0){
                    continue;
                }
                List<String> list = this.split(line);
                if (list != null && list.size() > 1) {
                    if ("medianote".equals(list.get(1))
                            || "mediantte".equals(list.get(1))
                            || "filehelper".equals(list.get(1))
                            || "mphelper".equals(list.get(1))
                            || "exmail_tttl".equals(list.get(1))
                            || "fltatbtttle".equals(list.get(1))
                            || "floatbottle".equals(list.get(1))
                            || "tmessage".equals(list.get(1))
                            || "weibt".equals(list.get(1))
                            || "qmessage".equals(list.get(1))
                            || "qqmail".equals(list.get(1))
                            || "weixin".equals(list.get(1))
                            || "fmessage".equals(list.get(1))) {
                        continue;
                    }
                    Wxlxr wxlxr = new Wxlxr();
                    wxlxr.setWeixin(regInfo.getWeixin());
                    wxlxr.setFileId(regInfo.getFileId());
                    wxlxr.setZh(list.size() > 1 ? list.get(1) : null);
                    wxlxr.setQq(list.size() > 2 ? list.get(2) : null);
                    wxlxr.setDh(list.size() > 3 ? list.get(3) : null);
                    wxlxr.setEmail(list.size() > 4 ? list.get(4) : null);
                    if (list.size() == 7) {
                        wxlxr.setWbo(list.size() > 5 ? list.get(5) : null);
                        wxlxr.setNc(list.size() > 6 ? list.get(6) : null);
                    } else if (list.size() == 8) {
                        wxlxr.setBm(list.size() > 5 ? list.get(5) : null);
                        wxlxr.setWbo(list.size() > 6 ? list.get(6) : null);
                        wxlxr.setNc(list.size() > 7 ? list.get(7) : null);
                    }

                    wxlxr.setCreateTime(new Date());
                    wxlxr.setSuspId(attachment.getSuspId());
                    wxlxr.setSuspName(attachment.getSuspName());
                    wxlxrList.add(wxlxr);
                }

            }
        }
    }
    private void parseQun(Attachment attachment, List<String> txtContent) {
        List<Wxqun> wxqunList = new ArrayList<>();
        regInfo.setWxqunList(wxqunList);
        for (int i = 0; i < txtContent.size(); i++) {
            String line = txtContent.get(i);
            if (StringUtils.isNotBlank(line)) {
                if(line.indexOf("微信群")>=0|| line.indexOf("帐号\t名称")>=0){
                    continue;
                }
                List<String> list = this.split(line);
                if (list != null) {
                    Wxqun wxlxr = new Wxqun();
                    wxlxr.setWeixin(regInfo.getWeixin());
                    wxlxr.setZh(list.get(1));
                    if (list.size() > 2) {
                        wxlxr.setMc(list.get(2));
                    }
                    if (list.size() > 3) {
                        wxlxr.setCjsj(list.get(3));
                    }
                    wxlxr.setFileId(regInfo.getFileId());
                    wxlxr.setSuspId(attachment.getSuspId());
                    wxlxr.setSuspName(attachment.getSuspName());
                    wxlxr.setCreateTime(new Date());
                    wxqunList.add(wxlxr);
                }

            }
        }
    }
    private void parseLoginip(Attachment attachment, List<String> txtContent) {
        List<Wxloginip> wxloginipList = new ArrayList<>();
        regInfo.setWxloginipList(wxloginipList);
        for (int i = 0; i < txtContent.size(); i++) {
            String line = txtContent.get(i);
            if (StringUtils.isNotBlank(line)) {
                if(line.indexOf("登录日志")>=0 || line.indexOf("时间\tIP")>=0){
                    continue;
                }
                List<String> list = this.split(line);
                if (list != null) {
                    Wxloginip wxlxr = new Wxloginip();
                    wxlxr.setWeixin(regInfo.getWeixin());
                    String cjsj = list.get(1);
                    if (cjsj.indexOf("+") > 10) {
                        cjsj = cjsj.substring(0, cjsj.indexOf("+") - 1).trim();
                    }
                    wxlxr.setCjsj(cjsj);
                    if (list.size() > 2) {
                        wxlxr.setIp(list.get(2));
                        wxlxr.setGsd(ipAddrUtil.findCityInfoString(wxlxr.getIp()));
                    }
                    wxlxr.setFileId(regInfo.getFileId());
                    wxlxr.setSuspId(attachment.getSuspId());
                    wxlxr.setSuspName(attachment.getSuspName());
                    wxlxr.setCreateTime(new Date());
                    wxloginipList.add(wxlxr);
                }
            }
        }
    }

    private List<String> split(String line) {
        String[] infos = line.split("\t");
        List<String> hylist = new ArrayList<>();
        for (String hy : infos) {
            hylist.add(hy.trim());
        }
        return hylist;
    }

}
