package com.anluy.admin.utils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anluy.admin.WebConfig;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

/**
 * 导出excel的工具类
 * Created by hc.zeng on 2019/4/2.
 */
@Component
public class ExcelExportUtil {

    /**
     * excel导出实现方法
     *
     * @param templatePath  模板xls或xlsx文件路径
     * @param beanParams        模板xls中对应要用到的集合;
     * @param out
     * @date 2018年7月24日 下午6:29:14
     */
    public static void exportExcel(String templatePath, Map<String, Object> beanParams,OutputStream out) throws IOException, InvalidFormatException {
        // 创建XLSTransformer对象
        XLSTransformer transformer = new XLSTransformer();
        try {
            InputStream inputStream = WebConfig.class.getResourceAsStream(templatePath);
            // 生成Excel文件
            Workbook workbook = transformer.transformXLS(inputStream, beanParams);
            OutputStream os = new BufferedOutputStream(out);
            workbook.write(os);
            inputStream.close();
            os.flush();
        } catch (Exception e) {
            throw e;
        }
    }
}