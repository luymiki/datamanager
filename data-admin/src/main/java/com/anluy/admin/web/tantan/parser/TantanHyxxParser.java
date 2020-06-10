package com.anluy.admin.web.tantan.parser;

import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.TantanHyxxInfo;
import com.anluy.admin.entity.TantanZhInfo;
import com.anluy.admin.utils.ExcelUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
public class TantanHyxxParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(TantanHyxxParser.class);
    private final Attachment attachment;

    public TantanHyxxParser(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<TantanHyxxInfo> parser(File file, FileManagerConfig fileManagerConfig) throws Exception {
        List<List<String>> stringList = ExcelUtils.read(file, 2);
        List<TantanHyxxInfo> tantanZhInfoList = parse(stringList);
        Map<String, byte[]> picMap = ExcelUtils.readDwr(file, 2);
        if (tantanZhInfoList != null) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < tantanZhInfoList.size(); i++) {
                byte[] data = picMap.get(i + "-7");
                if (data == null || data.length == 0) {
                    continue;
                }
                String fileName = tantanZhInfoList.get(i).getYhid() + ".jpg";
                String path = fileManagerConfig.getUploadDir() + "/" + attachment.getId() + "/" + fileName;
                FileUtils.writeByteArrayToFile(new File(path), data);
                tantanZhInfoList.get(i).setPic(fileName);
            }
        }
        return tantanZhInfoList;
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<TantanHyxxInfo> parser(String path, FileManagerConfig fileManagerConfig) throws Exception {
        return parser(new File(path), fileManagerConfig);
    }

    private List<TantanHyxxInfo> parse(List<List<String>> txtContent) throws Exception {
        if (txtContent.size() < 1) {
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<TantanHyxxInfo> hyxxInfos = new ArrayList<>();
        for (int i = 0; i < txtContent.size(); i++) {
            List<String> list = txtContent.get(i);
            if (list.size() < 7) {
                continue;
            }
            TantanHyxxInfo regInfo = new TantanHyxxInfo();
            regInfo.setFileId(attachment.getId());
            regInfo.setSuspId(attachment.getSuspId());
            regInfo.setSuspName(attachment.getSuspName());
            regInfo.setYhid(list.size() > 0 ? list.get(0) : null);
            regInfo.setNc(list.size() > 1 ? list.get(1) : null);
            regInfo.setXb(list.size() > 2 ? list.get(2) : null);
            regInfo.setCsrq(list.size() > 3 ? list.get(3) : null);
            regInfo.setZhhycs(list.size() > 4 ? list.get(4) : null);
            regInfo.setZcsjqh(list.size() > 5 ? list.get(5) : null);
            regInfo.setSjh(list.size() > 6 ? list.get(6) : null);
            hyxxInfos.add(regInfo);
        }
        return hyxxInfos;
    }

    public static void main(String[] args) {
        TantanHyxxParser zfbRegParser = new TantanHyxxParser(new Attachment());
        try {
            List info = zfbRegParser.parser("H:\\数据管理系统\\数据导入20191219\\copy.xlsx", null);
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
