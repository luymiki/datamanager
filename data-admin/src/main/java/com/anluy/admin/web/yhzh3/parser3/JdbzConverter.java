package com.anluy.admin.web.yhzh3.parser3;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.apache.commons.lang3.StringUtils;

public class JdbzConverter implements Converter<String> {


    @Override
    public Class supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public String convertToJavaData(CellData cellData, ExcelContentProperty contentProperty,
                                    GlobalConfiguration globalConfiguration) {
        try {
            String o = cellData.getStringValue();
            if(StringUtils.isBlank(o) && cellData.getNumberValue() != null){
                o = String.valueOf(cellData.getNumberValue().intValue());
            }else {
                o = o.trim();
                if("借".equals(o)){
                    return "出";
                }
                if("贷".equals(o)){
                    return "进";
                }
            }
            if(StringUtils.isNotBlank(o) && !"null".equals(o)){
                Integer jdlx = Integer.valueOf(o.endsWith(".00")?o.substring(0,o.length()-3):o);
                if(jdlx == 1){
                    return "出";
                }
                if(jdlx == 2){
                    return "进";
                }
                return "-";
            }
            return o;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CellData convertToExcelData(String value, ExcelContentProperty contentProperty,
                                       GlobalConfiguration globalConfiguration) {
        return new CellData(value);
    }
}
