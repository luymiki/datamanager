package com.anluy.admin.web.tantan.parser;

import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.TantanHyxxInfo;
import com.anluy.admin.entity.TantanLtjlInfo;
import com.anluy.admin.utils.ExcelUtils;
import org.apache.commons.io.FileUtils;
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
public class TantanLtjlParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(TantanLtjlParser.class);
    private final Attachment attachment;

    public TantanLtjlParser(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<TantanLtjlInfo> parser(File file, FileManagerConfig fileManagerConfig) throws Exception {
        List<List<String>> stringList = ExcelUtils.read(file, 3);
        return  parse(stringList);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<TantanLtjlInfo> parser(String path, FileManagerConfig fileManagerConfig) throws Exception {
        return parser(new File(path), fileManagerConfig);
    }

    private List<TantanLtjlInfo> parse(List<List<String>> txtContent) throws Exception {
        if (txtContent.size() < 1) {
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<TantanLtjlInfo> hyxxInfos = new ArrayList<>();
        for (int i = 0; i < txtContent.size(); i++) {
            List<String> list = txtContent.get(i);
            if (list.size() < 4) {
                continue;
            }
            TantanLtjlInfo regInfo = new TantanLtjlInfo();
            regInfo.setFileId(attachment.getId());
            regInfo.setSuspId(attachment.getSuspId());
            regInfo.setSuspName(attachment.getSuspName());
            regInfo.setFsid(list.size() > 0 ? list.get(0) : null);
            regInfo.setJsid(list.size() > 1 ? list.get(1) : null);
            if(list.size() > 2 ){
                Date date = sdf1.parse(list.get(2));
                regInfo.setFssj(date);
            }
            regInfo.setNr(list.size() > 3 ? list.get(3) : null);
            hyxxInfos.add(regInfo);
        }
        return hyxxInfos;
    }

    public static void main(String[] args) {
        TantanLtjlParser zfbRegParser = new TantanLtjlParser(new Attachment());
        try {
            List info = zfbRegParser.parser("H:\\数据管理系统\\数据导入20191219\\copy.xlsx", null);
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
