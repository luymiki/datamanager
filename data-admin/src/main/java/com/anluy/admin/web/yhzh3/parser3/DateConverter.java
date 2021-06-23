package com.anluy.admin.web.yhzh3.parser3;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;

import java.math.BigDecimal;
import java.util.Date;

public class DateConverter implements Converter<Date> {


    @Override
    public Class supportJavaTypeKey() {
        return Date.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Date convertToJavaData(CellData cellData, ExcelContentProperty contentProperty,
                                    GlobalConfiguration globalConfiguration) {
        try {
            if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
                return DateUtils.parseDate(cellData.getStringValue(), null);
            } else {
                return DateUtils.parseDate(cellData.getStringValue(),
                        contentProperty.getDateTimeFormatProperty().getFormat());
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CellData convertToExcelData(Date value, ExcelContentProperty contentProperty,
                                       GlobalConfiguration globalConfiguration) {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return new CellData(
                    BigDecimal.valueOf(DateUtil.getExcelDate(value, globalConfiguration.getUse1904windowing())));
        } else {
            return new CellData(BigDecimal.valueOf(
                    DateUtil.getExcelDate(value, contentProperty.getDateTimeFormatProperty().getUse1904windowing())));
        }
    }
}
