package com.anluy.admin.web.yhzh3.parser3;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class DoubleConverter implements Converter<Double> {


    @Override
    public Class supportJavaTypeKey() {
        return Double.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER;
    }

    @Override
    public Double convertToJavaData(CellData cellData, ExcelContentProperty contentProperty,
                                    GlobalConfiguration globalConfiguration) {
        try {
            if(cellData.getNumberValue()!=null){
                return cellData.getNumberValue().doubleValue();
            }else if(StringUtils.isNotBlank(cellData.getStringValue())){
                return Double.valueOf(cellData.getStringValue().replaceAll(",",""));
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CellData convertToExcelData(Double value, ExcelContentProperty contentProperty,
                                       GlobalConfiguration globalConfiguration) {
        return new CellData(BigDecimal.valueOf(value));
    }
}
