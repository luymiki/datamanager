package com.anluy.admin.web.weixin.parser;

import com.anluy.admin.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能说明：微信文件解析
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class WeiXinRegParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeiXinRegParser.class);
    private final String fileId;

    public WeiXinRegParser(String fileId) {
        this.fileId = fileId;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public WxregInfo parser(Attachment attachment, File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = null;
        List<String> stringList = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            stringList.add(line);
        }
        return parse(attachment, stringList);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public WxregInfo parser(Attachment attachment, String path) throws Exception {
        return parser(attachment, new File(path));
    }

    private WxregInfo parse(Attachment attachment, List<String> txtContent) {
        WxregInfo regInfo = new WxregInfo();
        regInfo.setFileId(fileId);
        regInfo.setSuspId(attachment.getSuspId());
        regInfo.setSuspName(attachment.getSuspName());
        regInfo.setCreateTime(new Date());

        List<Wxlxr> wxlxrList = new ArrayList<>();
        List<Wxqun> wxqunList = new ArrayList<>();
        List<Wxloginip> wxloginipList = new ArrayList<>();

        int infoIndex = -1;
        int lxrIndex = -1;
        int qunIndex = -1;
        int ipIndex = -1;
        for (int i = 0; i < txtContent.size(); i++) {
            String line = txtContent.get(i);
            if (StringUtils.isNotBlank(line)) {
                if (line.endsWith("基本资料：")) {
                    infoIndex = i + 1;//基本信息的行下标
                    continue;
                } else if (line.startsWith("联系人：")) {
                    infoIndex = -1;
                    lxrIndex = i + 2;//好友信息的行下标
                    continue;
                } else if (line.startsWith("微信群：")) {
                    lxrIndex = -1;
                    qunIndex = i + 2;//好友信息的行下标
                    continue;
                } else if (line.startsWith("登录日志：")) {
                    qunIndex = -1;
                    ipIndex = i + 2;//好友信息的行下标
                    continue;
                }
                if (infoIndex > 0) {
                    String[] infos = line.replace("\t", "").split("：");
                    if (infos.length == 2) {

                        switch (infos[0]) {
                            case "微信号": {
                                regInfo.setWeixin(infos[1]);
                                break;
                            }
                            case "QQ": {
                                regInfo.setQq(infos[1]);
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
                } else if (lxrIndex >0 && i>=lxrIndex  ) {//联系人
                    List<String> list = this.split(line);
                    if (list != null && list.size() == 8) {
                        if("medianote".equals(list.get(1))
                                || "floatbottle".equals(list.get(1))
                                || "qmessage".equals(list.get(1))
                                || "qqmail".equals(list.get(1))
                                || "fmessage".equals(list.get(1))){
                            continue;
                        }
                        Wxlxr wxlxr = new Wxlxr();
                        wxlxr.setWeixin(regInfo.getWeixin());
                        wxlxr.setZh(list.get(1));
                        wxlxr.setQq(list.get(2));
                        wxlxr.setDh(list.get(3));
                        wxlxr.setEmail(list.get(4));
                        wxlxr.setBm(list.get(5));
                        wxlxr.setWbo(list.get(6));
                        wxlxr.setNc(list.get(7));
                        wxlxr.setCreateTime(new Date());
                        wxlxr.setSuspId(attachment.getSuspId());
                        wxlxr.setSuspName(attachment.getSuspName());
                        wxlxrList.add(wxlxr);
                    }
                }else if (qunIndex >0 &&  i>=qunIndex) {//群
                    List<String> list = this.split(line);
                    if (list != null ) {
                        Wxqun wxlxr = new Wxqun();
                        wxlxr.setWeixin(regInfo.getWeixin());
                        wxlxr.setZh(list.get(1));
                        if(list.size()>2){
                            wxlxr.setMc(list.get(2));
                        }
                        if(list.size()>3){
                            wxlxr.setCjsj(list.get(3));
                        }
                        wxlxr.setSuspId(attachment.getSuspId());
                        wxlxr.setSuspName(attachment.getSuspName());
                        wxlxr.setCreateTime(new Date());
                        wxqunList.add(wxlxr);
                    }
                }else if (ipIndex >0 &&  i>=ipIndex) {//ip
                    List<String> list = this.split(line);
                    if (list != null ) {
                        Wxloginip wxlxr = new Wxloginip();
                        wxlxr.setWeixin(regInfo.getWeixin());
                        wxlxr.setCjsj(list.get(1));
                        if(list.size()>2){
                            wxlxr.setIp(list.get(2));
                        }
                        wxlxr.setSuspId(attachment.getSuspId());
                        wxlxr.setSuspName(attachment.getSuspName());
                        wxlxr.setCreateTime(new Date());
                        wxloginipList.add(wxlxr);
                    }
                }
            }
        }
        regInfo.setWxlxrList(wxlxrList);
        regInfo.setWxqunList(wxqunList);
        regInfo.setWxloginipList(wxloginipList);
        return regInfo;
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
