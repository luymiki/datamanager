package com.anluy.admin.web.yhzh2.parser;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.YhzhJylsInfo;
import com.anluy.admin.utils.ClassUtils;
import com.anluy.admin.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

/**
 * 功能说明：银行账号交易流水信息文件解析
 * <p>
 * Created by hc.zeng on 2018/6/25.
 */
public class YhzhJylsParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(YhzhJylsParser.class);
    private final Attachment attachment;

    public YhzhJylsParser(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<YhzhJylsInfo> parser(String ssyh, File file, String sheetName) throws Exception {
        List<List<String>> list = ExcelUtils.read(file, sheetName);
        return parse(ssyh, list);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<YhzhJylsInfo> parser(String ssyh, String path, String sheetName) throws Exception {
        return parser(ssyh, new File(path), sheetName);
    }

    private List<YhzhJylsInfo> parse(String ssyh, List<List<String>> txtContent) throws Exception {
        if (txtContent.size() < 2) {
            return new ArrayList<>();
        }
        List<String> titleList = txtContent.get(0);
        Map<Integer,ClassUtils.ClassFieldInfo> titleMapping = ClassUtils.loadIndexMapping(YhzhJylsInfo.class,titleList);
        Map<String,Function> functionMap = new HashMap<>();
        functionMap.put("交易金额",ClassUtils.FUNCTION_DOUBLE);
        functionMap.put("交易余额",ClassUtils.FUNCTION_DOUBLE);
        functionMap.put("交易时间",ClassUtils.FUNCTION_DATE_YYYYMMDDHHMMSS);
        functionMap.put("交易手续费金额",ClassUtils.FUNCTION_DOUBLE);

        List<YhzhJylsInfo> dataList = new ArrayList<>();
        for (int i = 1; i < txtContent.size(); i++) {
            List<String> list = txtContent.get(i);
            if (list.size() < 11) {
                continue;
            }
            YhzhJylsInfo jyls = new YhzhJylsInfo();
            jyls.setFileId(attachment.getId());
            jyls.setTags(attachment.getTags());
            jyls.setSuspId(attachment.getSuspId());
            jyls.setSuspName(attachment.getSuspName());
            jyls.setSsyh(ssyh);
            ClassUtils.setDataToObject(jyls,list,titleMapping,functionMap);
            if (jyls.getJyje()!=null) {
                if(jyls.getJyje()<0){
                    jyls.setJyje(jyls.getJyje()*-1);
                }
                jyls.setZc100(jyls.getJyje() % 100);
            }
            if (StringUtils.isNotBlank(jyls.getDfkh())) {
                jyls.setDsId(jyls.getDfkh());
            } else if (StringUtils.isNotBlank(jyls.getDfzh())) {
                jyls.setDsId(jyls.getDfzh());
            } else {
                jyls.setDsId("-");
            }
            dataList.add(jyls);
        }
        return dataList;
    }

    public static void main(String[] args) {
        YhzhJylsParser zfbRegParser = new YhzhJylsParser(new Attachment());
        try {
            List<YhzhJylsInfo> info = zfbRegParser.parser("工商银行", "H:\\数据管理系统\\数据导入20180903\\工行客户及交易信息（9-20，1516条）.xls", "交易信息");
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
