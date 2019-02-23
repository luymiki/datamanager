package com.anluy.admin.web.qq.parser;

import com.anluy.admin.entity.QQRegInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class QQRegParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(QQRegParser.class);
    private final String fileId;

    public QQRegParser(String fileId) {
        this.fileId = fileId;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public QQRegInfo parser(File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
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
    public QQRegInfo parser(String path) throws Exception {
        return parser(new File(path));
    }

    private QQRegInfo parse(List<String> txtContent) {
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QQRegInfo regInfo = new QQRegInfo();
        regInfo.setFileId(fileId);
        int infoIndex = -1;
        int hyIndex = -1;
        int cjqIndex = -1;
        int jrqIndex = -1;
        for (int i = 0; i < txtContent.size(); i++) {
            String line = txtContent.get(i);
            if (StringUtils.isNotBlank(line)) {
                if (line.startsWith("QQ\t昵称")) {
                    infoIndex = i + 1;//基本信息的行下标
                    continue;
                } else if (line.endsWith("的好友号码")) {
                    hyIndex = i + 2;//好友信息的行下标
                    continue;
                } else if (line.startsWith("创建的群：")) {
                    cjqIndex = i + 1;//好友信息的行下标
                    continue;
                } else if (line.endsWith("加入的群：")) {
                    jrqIndex = i + 1;//好友信息的行下标
                    continue;
                }
                if (infoIndex == i) {
                    infoIndex = -1;
                    String[] infos = line.split("\t");
                    if(infos.length != 17){
                        throw new RuntimeException("文件格式不正确，不能解析");
                    }
                    regInfo.setQq(infos[0]);
                    regInfo.setName(infos[1]);
                    regInfo.setGj(infos[2]);
                    regInfo.setSf(infos[3]);
                    regInfo.setYzbm(infos[4]);
                    regInfo.setDz(infos[5]);
                    regInfo.setDh(infos[6]);
                    if (StringUtils.isNotBlank(infos[7])) {
                        String[] sc = infos[7].split(":");
                        if (sc.length == 2) {
                            try {
                                regInfo.setCsrq(simpleDateFormat2.format(simpleDateFormat1.parse(sc[1])));
                            } catch (ParseException e) {
                                LOGGER.error("出生时间解析异常 " + infos[7] + " -> yyyy-MM-dd ");
                            }
                        }

                    }
                    regInfo.setXb(infos[8]);
                    regInfo.setXm(infos[9]);
                    regInfo.setEmail(infos[10]);
                    regInfo.setHome(infos[11]);
                    regInfo.setCs(infos[13]);
                    regInfo.setBycs(infos[14]);
                    regInfo.setXz(infos[15]);
                    if (StringUtils.isNotBlank(infos[16])) {
                        try {
                            regInfo.setZcsj(simpleDateFormat2.format(simpleDateFormat2.parse(infos[16])));
                        } catch (ParseException e) {
                            LOGGER.error("注册时间解析异常 " + infos[16] + " -> yyyy-MM-dd HH:mm:ss");
                        }
                    }
                } else if (hyIndex == i) {//好友
                    hyIndex = -1;
                    regInfo.setQqhy(this.split(line));
                } else if (cjqIndex == i) {//创建的群
                    hyIndex = -1;
                    regInfo.setCjqh(this.split(line));
                } else if (jrqIndex == i) {//加入的群
                    hyIndex = -1;
                    regInfo.setJrqh(this.split(line));
                }


            }
        }

        return regInfo;
    }

    private List<String> split(String line) {
        String[] infos = line.split("\t");
        List<String> hylist = new ArrayList<>();
        for (String hy : infos) {
            if("无创建群加入的群：".equals(hy)){
                continue;
            }
            hylist.add(hy.trim());
        }
        return hylist;
    }

}
