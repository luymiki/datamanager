package com.anluy.admin.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 功能说明：excel解析工具
 * <p>
 * Created by hc.zeng on 2018/4/6.
 */
public class ExcelUtils {

    public static List<List<String>> read(File file) {
        return read(file, 0);
    }

    public static List<List<String>> read(File file, String sheetName) {
        Sheet sheet = null;
        Workbook wb = readExcel(file);
        int sheets = wb.getNumberOfSheets();
        int sheetIndex = 0;
        for (int i = 0; i < sheets; i++) {
            //获取第一个sheet
            sheet = wb.getSheetAt(i);
            String name = sheet.getSheetName();
            if (name.equals(sheetName)) {
                sheetIndex = i;
                break;
            }
        }
        if (wb != null) {
            sheet = wb.getSheetAt(sheetIndex);
            return read(sheet);
        }
        return new ArrayList<>();
    }

    public static List<List<String>> read(File file, int sheetIndex) {
        if (checkHtml(file)) {
            return readHtml(file);
        } else {
            Workbook wb = readExcel(file);
            if (wb != null) {
                //获取第一个sheet
                Sheet sheet = wb.getSheetAt(sheetIndex);
                return read(sheet);
            }
        }
        return new ArrayList<>();
    }

    private static List<List<String>> read(Sheet sheet) {
        List<List<String>> dataList = new ArrayList<>();//用来存放表中数据
        //获取最大行数
        int rownum = sheet.getPhysicalNumberOfRows();
        Row row = null;
        String cellData = null;
        for (int i = 0; i <= rownum; i++) {
            List<String> list = new ArrayList<>();
            row = sheet.getRow(i);
            if (row != null) {
                //System.out.println(i+":"+row.getPhysicalNumberOfCells());
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    cellData = (String) getCellFormatValue(row.getCell(j));
                    if ("-".equals(cellData)) {
                        list.add(null);
                    } else {
                        list.add(cellData);
                    }
                }
            } else {
                continue;
            }
            dataList.add(list);
        }
        return dataList;
    }

    public static List<Map<String, String>> read(String columns[], File file) {
        if (columns == null) {
            throw new RuntimeException("字段列表为空");
        }
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        List<List<String>> dataList = read(file);
        for (int i = 0; i < dataList.size(); i++) {
            List<String> row = dataList.get(i);
            Map<String, String> map = new LinkedHashMap<String, String>();
            for (int j = 0; j < columns.length; j++) {
                if (row.size() > j) {
                    map.put(columns[j], row.get(j));
                } else {
                    map.put(columns[j], null);
                }
            }
            resultList.add(map);
        }
        return resultList;
    }

    //读取excel
    public static Workbook readExcel(File file) {
        if (file == null) {
            return null;
        }
        String extString = file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase();
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            if (".xls".equals(extString)) {
                return new HSSFWorkbook(is);
            } else if (".xlsx".equals(extString)) {
                return new XSSFWorkbook(is);
            } else {
                throw new RuntimeException("不支持的文件格式");
            }
        } catch (Exception e) {
            throw new RuntimeException("文件读取异常", e);
        }
    }

    public static Object getCellFormatValue(Cell cell) {
        Object cellValue = null;
        if (cell != null) {
            //判断cell类型
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC: {
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        cellValue = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
                    } else {
//                        cellValue = cell.getNumericCellValue();
//                        DecimalFormat df = new DecimalFormat("0");
//                        cellValue = df.format(cellValue);
                        BigDecimal bg = new BigDecimal(String.valueOf(cell.getNumericCellValue()));
                        cellValue = bg.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
                    }

                    break;
                }
                case Cell.CELL_TYPE_FORMULA: {
                    //判断cell是否为日期格式
                    if (DateUtil.isCellDateFormatted(cell)) {
                        //转换为日期格式YYYY-mm-dd
                        cellValue = cell.getDateCellValue();
                    } else {
                        //数字
                        BigDecimal bg = new BigDecimal(String.valueOf(cell.getNumericCellValue()));
                        cellValue = bg.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING: {
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                }
                default:
                    cellValue = "";
            }
        } else {
            cellValue = "";
        }
        return cellValue;
    }


    /**
     * 校验文件是不是excel导出的HTML文件
     *
     * @param file
     * @return
     */
    public static boolean checkHtml(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line = bufferedReader.readLine();
            while (line != null) {
                if (StringUtils.isNotBlank(line) && line.trim().toLowerCase().startsWith("<html")) {
                    return true;
                }
                line = bufferedReader.readLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static List<List<String>> readHtml(File file) {
        try {
            List<List<String>> list = new ArrayList<>();
            Document document = Jsoup.parse(file, "GBK");
            Elements h4s = document.getElementsByTag("h4");
            if (!h4s.isEmpty()) {
                List<String> trList = new ArrayList<>();
                String title = h4s.get(0).text();
                trList.add(title);
                list.add(trList);
            }
            Elements containers = document.getElementsByTag("tr");
            if (!containers.isEmpty()) {
                containers.forEach(element -> {
                    List<String> trList = new ArrayList<>();
                    list.add(trList);
                    Elements trElements = element.getElementsByTag("td");
                    trElements.forEach(tr -> {
                        trList.add(tr.attr("x:str"));
                    });
                });
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException("文件读取失败 " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        readHtml(new File("I:\\update-20190112\\17817815981 - 副本.xml"));
        //System.out.println(checkHtml(new File("I:\\update-20190112\\17817815981 - 副本.xml")));
    }

}
