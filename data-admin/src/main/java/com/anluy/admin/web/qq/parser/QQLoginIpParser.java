package com.anluy.admin.web.qq.parser;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.QQLoginInfo;
import com.anluy.admin.entity.QQLoginIpInfo;
import com.anluy.admin.entity.QQRegInfo;
import com.anluy.admin.utils.IPAddrUtil;
import com.google.common.hash.HashCode;
import net.ipip.ipdb.CityInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class QQLoginIpParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(QQLoginIpParser.class);
    private final String fileId;
    private IPAddrUtil ipAddrUtil;

    public QQLoginIpParser(String fileId, IPAddrUtil ipAddrUtil) {
        this.fileId = fileId;
        this.ipAddrUtil = ipAddrUtil;
    }

    /**
     * 解析文件
     *
     * @param attachment
     * @param path
     * @return
     * @throws Exception
     */
    public QQLoginInfo parser(Attachment attachment, String path) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path)));
        String line = null;
        List<String> stringList = new ArrayList<>();
        Set<Integer> hset = new HashSet<>();
        while ((line = bufferedReader.readLine()) != null) {
            HashCodeBuilder builder = new HashCodeBuilder();
            int hs = builder.append(line).toHashCode();
            if (!hset.contains(hs)) {
                stringList.add(line);
                hset.add(hs);
            }
        }
        return parse(attachment, stringList);
    }

    private QQLoginInfo parse(Attachment attachment, List<String> txtContent) throws ParseException {

        QQLoginInfo loginInfo = new QQLoginInfo();
        loginInfo.setFileId(fileId);
        loginInfo.setSuspId(attachment.getSuspId());
        loginInfo.setSuspName(attachment.getSuspName());
        Set<String> ipset = new HashSet<>();
        List<QQLoginIpInfo> iplist = new ArrayList<>();
        int infoIndex = -1;
        for (int i = 0; i < txtContent.size(); i++) {
            String line = txtContent.get(i);
            if (StringUtils.isNotBlank(line)) {
                if (line.startsWith("QQ")) {
                    infoIndex = i + 1;//基本信息的行下标
                    continue;
                }
                if (infoIndex > 0) {
                    String[] infos = line.split("\t");
                    if (infos.length != 5) {
                        throw new RuntimeException("文件格式不正确，不能解析");
                    }
                    QQLoginIpInfo ipInfo = split(infos);
                    ipInfo.setSuspId(attachment.getSuspId());
                    ipInfo.setSuspName(attachment.getSuspName());
                    ipset.add(ipInfo.getIp());
                    if (StringUtils.isBlank(loginInfo.getQq())) {
                        loginInfo.setQq(ipInfo.getQq());
                    }
                    iplist.add(ipInfo);
                }
            }
        }
        loginInfo.setIpinfoList(iplist);
        List<String> list = new ArrayList<>();
        ipset.forEach(s -> {
            list.add(s);
        });
        loginInfo.setIpList(list);
        loginInfo.setSize(iplist.size());
        return loginInfo;
    }

    private QQLoginIpInfo split(String[] infos) throws ParseException {
        QQLoginIpInfo info = new QQLoginIpInfo();
        if (infos.length == 5) {
            info.setCreateTime(new Date());
            info.setFileId(fileId);
            info.setQq(infos[0]);
            info.setIp(infos[1]);
            info.setGsd(ipAddrUtil.findCityInfoString(info.getIp()));
            info.setLoginTime(infos[2]);
            info.setLogoutTime(infos[3]);
            info.setLoginType(infos[4]);
        }
        return info;
    }

}
