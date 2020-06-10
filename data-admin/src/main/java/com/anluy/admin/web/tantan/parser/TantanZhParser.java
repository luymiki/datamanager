package com.anluy.admin.web.tantan.parser;

import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.TantanZhInfo;
import com.anluy.admin.entity.ZfbZhInfo;
import com.anluy.admin.utils.CSVReader;
import com.anluy.admin.utils.ExcelUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 功能说明：探探账户明细文件解析
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class TantanZhParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(TantanZhParser.class);
    private final Attachment attachment;

    public TantanZhParser(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public TantanZhInfo parser(File file, FileManagerConfig fileManagerConfig) throws Exception {
        List<List<String>> stringList = ExcelUtils.read(file);
        TantanZhInfo tantanZhInfo = parse(stringList);
        List<List<String>> stringList2 = ExcelUtils.read(file, 1);
        List<List<String>> stringList4 = ExcelUtils.read(file, 4);
        Map<String, byte[]> picMap = ExcelUtils.readDwr(file, 1);
        if (tantanZhInfo != null) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < stringList2.size(); i++) {
                byte[] data = picMap.get(i + "-1");
                if (data == null || data.length == 0) {
                    continue;
                }
                String time = stringList2.get(i).get(0);
                Date date = sdf1.parse(time);
                String fileName = DateFormatUtils.format(date, "yyyyMMddHHmmss") + ".jpg";
                String path = fileManagerConfig.getUploadDir() + "/" + attachment.getId() + "/" + fileName;
                FileUtils.writeByteArrayToFile(new File(path), data);
                tantanZhInfo.addPic(fileName);

            }
            for (List<String> row : stringList4) {
                if(row.size()<2){
                    continue;
                }
                String key = row.get(0);
                String val = row.get(1);
                if(StringUtils.isNotBlank(key)&& StringUtils.isNotBlank(val)){
                    if(key.toUpperCase().startsWith("IMEI")){
                        tantanZhInfo.addImei(val);
                    } else if(key.toUpperCase().startsWith("IMSI")){
                        tantanZhInfo.addImsi(val);
                    }
                }
            }
        }
        return tantanZhInfo;
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public TantanZhInfo parser(String path, FileManagerConfig fileManagerConfig) throws Exception {
        return parser(new File(path), fileManagerConfig);
    }

    private TantanZhInfo parse(List<List<String>> txtContent) throws Exception {
        if (txtContent.size() < 2) {
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TantanZhInfo regInfo = new TantanZhInfo();
        regInfo.setFileId(attachment.getId());
        regInfo.setTags(attachment.getTags());
        regInfo.setSuspId(attachment.getSuspId());
        regInfo.setSuspName(attachment.getSuspName());
        for (int i = 0; i < txtContent.size(); i++) {
            List<String> list = txtContent.get(i);
            if (list.size() < 2) {
                continue;
            }
            String title = list.get(0).trim();
            String val = list.get(1).trim();
            switch (title) {
                case "用户ID": {
                    regInfo.setYhid(val);
                    break;
                }
                case "昵称": {
                    regInfo.setNc(val);
                    break;
                }
                case "注册手机区号": {
                    regInfo.setZcsjqh(val);
                    break;
                }
                case "手机号": {
                    regInfo.setSjh(val);
                    break;
                }
                case "注册IP": {
                    regInfo.setIp(val);
                    break;
                }
                case "最后活跃坐标": {
                    regInfo.setZhhyzb(val);
                    break;
                }
                case "最后活跃城市": {
                    regInfo.setZhhycs(val);
                    break;
                }
                case "性别": {
                    regInfo.setXb(val);
                    break;
                }
                case "出生日期": {
                    regInfo.setCsrq(val);
                    break;
                }
                case "当前状态": {
                    regInfo.setDqzt(val);
                    break;
                }
                case "注册时间": {
                    Date date = sdf1.parse(val);
                    regInfo.setZcsj(date);
                    break;
                }
                case "最后活跃时间": {
                    Date date = sdf1.parse(val);
                    regInfo.setZhhysj(date);
                    break;
                }
            }

        }
        return regInfo;
    }

    public static void main(String[] args) {
        TantanZhParser zfbRegParser = new TantanZhParser(new Attachment());
        try {
            TantanZhInfo info = zfbRegParser.parser("H:\\数据管理系统\\数据导入20191219\\copy.xlsx", null);
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
