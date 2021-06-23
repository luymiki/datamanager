package com.anluy.admin.web.yhzh3.parser3;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.doubleconverter.DoubleNumberConverter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.read.listener.ReadListener;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.YhzhJylsInfo;
import com.anluy.admin.utils.ClassUtils;
import com.anluy.admin.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

/**
 * 功能说明：银行账号交易流水信息文件解析
 * <p>
 * Created by hc.zeng on 2018/6/25.
 */
public class YhzhJylsParser3 {
    private static final Logger LOGGER = LoggerFactory.getLogger(YhzhJylsParser3.class);
    private final Attachment attachment;

    public YhzhJylsParser3(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<YhzhJylsInfo3> parser(String ssyh, File file, int sheetIndex) throws Exception {
        return parse(ssyh, file, sheetIndex);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<YhzhJylsInfo3> parser(String ssyh, String path, int sheetIndex) throws Exception {
        return parser(ssyh, new File(path), sheetIndex);
    }

    private List<YhzhJylsInfo3> parse(String ssyh, File file, int sheetIndex) throws Exception {
        try (InputStream is = new FileInputStream(file)) {
            List<Object> txtContent = EasyExcel.read(is).head(YhzhJylsInfo3.class).sheet(sheetIndex).headRowNumber(1).autoTrim(true).registerReadListener(new ReadListener() {
                @Override
                public void onException(Exception exception, AnalysisContext context) throws Exception {

                }

                @Override
                public void invokeHead(Map headMap, AnalysisContext context) {

                }

                @Override
                public void invoke(Object data, AnalysisContext context) {
                    YhzhJylsInfo3 jyls = (YhzhJylsInfo3) data;
                    jyls.setFileId(attachment.getId());
                    jyls.setTags(attachment.getTags());
                    jyls.setSuspId(attachment.getSuspId());
                    jyls.setSuspName(attachment.getSuspName());
                    jyls.setSsyh(ssyh);
                    if (jyls.getJyje() != null) {
                        if (jyls.getJyje() < 0) {
                            jyls.setJyje(jyls.getJyje() * -1);
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
                    if (StringUtils.isBlank(jyls.getKh()) && StringUtils.isNotBlank(jyls.getZh())) {
                        jyls.setKh(jyls.getZh());
                    }
                    if (StringUtils.isBlank(jyls.getZh()) && StringUtils.isNotBlank(jyls.getKh())) {
                        jyls.setZh(jyls.getKh());
                    }
                    try{
                        if(ssyh.indexOf("农业")>=0 && jyls.getKh().startsWith("000")){
                            jyls.setKh("41"+jyls.getKh());
                        }
                        if(ssyh.indexOf("农业")>=0 && jyls.getZh().startsWith("000")){
                            jyls.setZh("41"+jyls.getZh());
                        }
                    }catch (RuntimeException e){
                        throw e;
                    }

                    if (StringUtils.isNotBlank(jyls.getJysjStr()) && !"null".equals(jyls.getJysjStr()) && jyls.getJysjStr().length()>=14) {
                        jyls.setJysj((Date) ClassUtils.FUNCTION_DATE_YYYYMMDDHHMMSS.apply(jyls.getJysjStr()));
                    } else if (StringUtils.isNotBlank(jyls.getJyrq()) && !"null".equals(jyls.getJyrq())) {
                        if (StringUtils.isNotBlank(jyls.getJysjStr()) && !"null".equals(jyls.getJysjStr())) {
                            if (jyls.getJysjStr().indexOf(":") > 0) {
                                String jysj = String.format("%s %s", jyls.getJyrq(), jyls.getJysjStr());
                                jyls.setJysj((Date) ClassUtils.FUNCTION_DATE_YYYYMMDDHHMMSS_GG.apply(jysj));
                            } else if (jyls.getJyrq().length() == 8) {
                                String jysj = String.format("%s%s", _zore(jyls.getJyrq(), 8), _zore(jyls.getJysjStr(), 6));
                                jyls.setJysj((Date) ClassUtils.FUNCTION_DATE_YYYYMMDDHHMMSS.apply(jysj));
                            }
                        } else {
                            if (jyls.getJyrq().length() == 8) {
                                jyls.setJysj((Date) ClassUtils.FUNCTION_DATE_YYYYMMDD.apply(_zore(jyls.getJyrq(), 8)));
                            } else if (jyls.getJyrq().indexOf("-") > 0) {
                                jyls.setJysj((Date) ClassUtils.FUNCTION_DATE_YYYY_MM_DD.apply(jyls.getJyrq()));
                            }
                        }
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {

                }

                @Override
                public boolean hasNext(AnalysisContext context) {
                    return true;
                }

                private String _zore(String s, int t) {
                    if (s.length() >= t) {
                        return s;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(s);
                    for (int i = s.length(); i < t; i++) {
                        sb.append("0");
                    }
                    return sb.toString();
                }
            }).doReadSync();
            if (txtContent == null || txtContent.isEmpty()) {
                return new ArrayList<>();
            }
            return (List) txtContent;
        }
    }

    public static void main(String[] args) {
        YhzhJylsParser3 zfbRegParser = new YhzhJylsParser3(new Attachment());
        try {
            List<YhzhJylsInfo3> info = zfbRegParser.parser("农业银行", "E:\\xinghuo\\datamanager\\数据导入20210603银行测试数据\\健身.xls", 2);
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
