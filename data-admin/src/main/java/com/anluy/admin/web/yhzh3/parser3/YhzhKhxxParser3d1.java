package com.anluy.admin.web.yhzh3.parser3;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.HeadKindEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.property.ExcelReadHeadProperty;
import com.alibaba.excel.util.ConverterUtils;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.YhzhKhxxInfo;
import com.anluy.admin.utils.ClassUtils;
import com.anluy.admin.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 功能说明：银行账号开户信息文件解析
 * <p>
 * Created by hc.zeng on 2018/6/25.
 */
public class YhzhKhxxParser3d1 {
    private static final Logger LOGGER = LoggerFactory.getLogger(YhzhKhxxParser3d1.class);
    private final Attachment attachment;

    public YhzhKhxxParser3d1(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<YhzhKhxxInfo3> parser(String ssyh, String path, int sheetIndex) throws Exception {
        return parse(ssyh, new File(path), sheetIndex);
    }

    private List<YhzhKhxxInfo3> parse(String ssyh, File file ,int sheetIndex) throws Exception {
        try (InputStream is = new FileInputStream(file)) {
            List<Object> txtContent = EasyExcel.read(is).head(YhzhKhxxInfo3.class).sheet(sheetIndex).autoTrim(true).headRowNumber(2).registerReadListener(new ReadListener() {
                @Override
                public void onException(Exception exception, AnalysisContext context) throws Exception {

                }

                @Override
                public void invoke(Object data, AnalysisContext context) {
                    YhzhKhxxInfo3 khxx = (YhzhKhxxInfo3)data;
                    khxx.setFileId(attachment.getId());
                    khxx.setTags(attachment.getTags());
                    khxx.setSuspId(attachment.getSuspId());
                    khxx.setSuspName(attachment.getSuspName());
                    khxx.setSsyh(ssyh);
                    if(StringUtils.isBlank(khxx.getKh()) && StringUtils.isNotBlank(khxx.getZh())){
                        khxx.setKh(khxx.getZh());
                    }
                    if(StringUtils.isBlank(khxx.getZh()) && StringUtils.isNotBlank(khxx.getKh())){
                        khxx.setZh(khxx.getKh());
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {

                }

                @Override
                public boolean hasNext(AnalysisContext context) {
                    return true;
                }
                Map<Integer, ExcelContentProperty> contentPropertyMap =new HashMap();
                Map<Integer, Head> headMap  =new HashMap();
                @Override
                public void invokeHead(Map headMap, AnalysisContext context) {
                    if(this.headMap.isEmpty()){
                        buildHead(context,headMap,contentPropertyMap,this.headMap);
                    }else {
                        this.contentPropertyMap.putAll(context.currentReadHolder().excelReadHeadProperty().getContentPropertyMap());
                        this.headMap.putAll(context.currentReadHolder().excelReadHeadProperty().getHeadMap());
                        context.currentReadHolder().excelReadHeadProperty().getContentPropertyMap().putAll(this.contentPropertyMap);
                        context.currentReadHolder().excelReadHeadProperty().getHeadMap().putAll(this.headMap);
                    }
                }
            }).doReadSync();

            if (txtContent ==null|| txtContent.isEmpty() ) {
                return new ArrayList<>();
            }

            List<YhzhKhxxInfo3> list = new ArrayList<>();
            txtContent.forEach(k->{
                YhzhKhxxInfo3 khxx = (YhzhKhxxInfo3)k;
                if(khxx.getKhrq() == null){
                    return;
                }
                list.add(khxx);
            });

            return list;
        }
    }
    private void buildHead(AnalysisContext analysisContext, Map<Integer, CellData> cellDataMap,Map<Integer, ExcelContentProperty> contentPropertyMap,Map<Integer, Head> headMap) {
        if (!HeadKindEnum.CLASS.equals(analysisContext.currentReadHolder().excelReadHeadProperty().getHeadKind())) {
            return;
        }
        Map<Integer, String> dataMap = ConverterUtils.convertToStringMap(cellDataMap, analysisContext.currentReadHolder());
        ExcelReadHeadProperty excelHeadPropertyData = analysisContext.readSheetHolder().excelReadHeadProperty();
        Map<Integer, Head> headMapData = excelHeadPropertyData.getHeadMap();
        Map<Integer, ExcelContentProperty> contentPropertyMapData = excelHeadPropertyData.getContentPropertyMap();
        for (Map.Entry<Integer, Head> entry : headMapData.entrySet()) {
            Head headData = entry.getValue();
            if (headData.getForceIndex() || !headData.getForceName()) {
                headMap.put(entry.getKey(), headData);
                contentPropertyMap.put(entry.getKey(), contentPropertyMapData.get(entry.getKey()));
                continue;
            }
            String headName = headData.getHeadNameList().get(0);

            for (Map.Entry<Integer, String> stringEntry : dataMap.entrySet()) {
                String headString = stringEntry.getValue();
                Integer stringKey = stringEntry.getKey();
                if (com.alibaba.excel.util.StringUtils.isEmpty(headString)) {
                    continue;
                }
                if (analysisContext.currentReadHolder().globalConfiguration().getAutoTrim()) {
                    headString = headString.trim();
                }
                if (headName.equals(headString)) {
                    headData.setColumnIndex(stringKey);
                    headMap.put(stringKey, headData);
                    contentPropertyMap.put(stringKey, contentPropertyMapData.get(entry.getKey()));
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        YhzhKhxxParser3d1 zfbRegParser = new YhzhKhxxParser3d1(new Attachment());
        try {
            List<YhzhKhxxInfo3> info = zfbRegParser.parser("招商银行", "H:\\数据管理系统\\数据导入20210603银行测试数据\\招商银行.xlsx", 1);
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
