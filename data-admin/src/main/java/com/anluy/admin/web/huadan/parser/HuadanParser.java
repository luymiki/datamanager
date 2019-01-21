package com.anluy.admin.web.huadan.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.CftRegInfo;
import com.anluy.admin.entity.HuadanInfo;
import com.anluy.admin.entity.HuadanList;
import com.anluy.admin.utils.ExcelUtils;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * 功能说明：话单文件解析
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class HuadanParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(HuadanParser.class);
    /**
     * 一小时的秒数
     */
    private static final int HOUR_SECOND = 60 * 60;

    /**
     * 一分钟的秒数
     */
    private static final int MINUTE_SECOND = 60;

    private final String fileId;

    public HuadanParser(String fileId) {
        this.fileId = fileId;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public HuadanInfo parser(Attachment attachment, File file) throws Exception {
        List<List<String>> list = ExcelUtils.read(file);
        return parse(attachment, attachment.getFolder(), list);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public HuadanInfo parser(Attachment attachment, String path) throws Exception {
        return parser(attachment, new File(path));
    }

    private HuadanInfo parse(Attachment attachment, String yys, List<List<String>> datalist) {
        if (datalist.size() < 2) {
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        HuadanInfo huadanInfo = new HuadanInfo();
        huadanInfo.setSuspId(attachment.getSuspId());
        huadanInfo.setSuspName(attachment.getSuspName());
        huadanInfo.setFileId(fileId);
        huadanInfo.setCreateTime(new Date());
        huadanInfo.setTags(attachment.getTags());
        if ("移动".equals(yys)) {
            return parserYd(huadanInfo, datalist);
        } else if ("联通".equals(yys)) {
            return parserLt(huadanInfo, datalist);
        } else if ("电信".equals(yys)) {
            return parserDx(huadanInfo, datalist);
        }
        return null;
    }

    /**
     * 解析电信话单
     *
     * @param datalist
     * @return
     */
    private HuadanInfo parserDx(HuadanInfo huadanInfo, List<List<String>> datalist) {
        String zjhm = null;
        int start = 6;
        for (int i = 0; i < datalist.size(); i++) {
            List<String> row = datalist.get(i);
            if (row.get(0) != null && row.get(0).startsWith("客户名称")) {
                zjhm = row.get(3).split("\\.")[0];
            } else if (row.get(0) != null && row.get(0).startsWith("序号")) {
                start = i + 1;
                break;
            }
        }
        huadanInfo.setYys("电信");
        huadanInfo.setZjhm(zjhm);

        List<String> columns = new ArrayList<>(20);
        for (int i = 0; i <20 ; i++) {
            columns.add(null);
        }
        columns.set(1, "thlx");
        columns.set(9, "thdd");
//        columns.set(2, "zjhm");
        columns.set(12, "ddhmgsd");
        columns.set(3, "ddhm");
        columns.set(4, "kssj");
        columns.set(5, "thsc");
        columns.set(8, "hjlx");
        columns.set(17, "xqh");
        List<HuadanList> huadanLists = new ArrayList<>();
        for (int i = start; i < datalist.size(); i++) {
            List<String> row = datalist.get(i);
            JSONObject data = new JSONObject();
            for (int j = 0; j < columns.size(); j++) {
                if (StringUtils.isNotBlank(columns.get(j)) && row.size()>j) {
                    Object value = row.get(j);
                    if("thsc".equals(columns.get(j))){
                        value = splitSecond((String)value);
                    }else if("ddhm".equals(columns.get(j)) && value instanceof String && StringUtils.isNotBlank((String)value)){
                        value = value.toString().replace("0086|\\+86","");
                    }
                    data.put(columns.get(j), value);
                }
            }
            data.put("yys",huadanInfo.getYys());
            data.put("susp_id",huadanInfo.getSuspId());
            data.put("susp_name",huadanInfo.getSuspName());
            data.put("file_id",huadanInfo.getFileId());
            data.put("tags",huadanInfo.getTags());
            data.put("zjhm",huadanInfo.getZjhm());
            huadanLists.add(JSON.toJavaObject(data, HuadanList.class));
        }
        huadanInfo.setSize(huadanLists.size());
        huadanInfo.setHuadanLists(huadanLists);
        return huadanInfo;
    }

    /**
     * 解析移动话单
     *
     * @param datalist
     * @return
     */
    private HuadanInfo parserYd(HuadanInfo huadanInfo,  List<List<String>> datalist) {
        List<String> columns = new ArrayList<>(20);
        for (int i = 0; i <8 ; i++) {
            columns.add(null);
        }
        String zjhm = null;
        int start = 6;
        for (int i = 0; i < datalist.size(); i++) {
            List<String> row = datalist.get(i);
            if (row.get(0) != null && row.get(0).startsWith("手机号码:")) {
                //手机号码:13530329368 开始时间：20171030 结束时间：20180130 查询日期：2018-01-30
                zjhm = row.get(0).split(" ")[0].split(":")[1];
            } else if (row.get(0) != null && row.get(0).startsWith("通话合并短彩详单")) {
                start = i + 2;
                columns.set(5, "thlx");
                columns.set(1, "thdd");
                columns.set(3, "ddhm");
                columns.set(0, "kssj");
                columns.set(4, "thsc");
                columns.set(2, "hjlx");
                break;
            }else if(row.get(0) !=null && row.get(0).startsWith("通话详单")){
                start = i + 2;
                columns.set(6, "thlx");
                columns.set(2, "thdd");
                columns.set(4, "ddhm");
                columns.set(1, "kssj");
                columns.set(5, "thsc");
                columns.set(3, "hjlx");
                break;
            }else {
                for (int j = 0; j < row.size() ; j++) {
                    if(row.get(j)!=null && row.get(j).startsWith("手机号码:")){
                        zjhm = row.get(j).replace("手机号码:","").trim();
                        break;
                    }
                }
            }
        }
        huadanInfo.setYys("移动");
        huadanInfo.setZjhm(zjhm);
        setHuadanList(start,huadanInfo,datalist,columns);
        return huadanInfo;
    }

    /**
     * 解析联通话单
     *
     * @param datalist
     * @return
     */
    private HuadanInfo parserLt(HuadanInfo huadanInfo,  List<List<String>> datalist) {
        String zjhm = null;
        int start = 6;
        for (int i = 0; i < datalist.size(); i++) {
            List<String> row = datalist.get(i);
            if (row.get(1) != null && row.get(1).startsWith("本机号")) {
                zjhm = datalist.get(i+1).get(1);
                start = i + 1;
                break;
            }
        }
        huadanInfo.setYys("联通");
        huadanInfo.setZjhm(zjhm);

        List<String> columns = new ArrayList<>(9);
        for (int i = 0; i <9 ; i++) {
            columns.add(null);
        }
        columns.set(7, "thlx");
        //columns.set(1, "thdd");
        //columns.set(2, "zjhm");
        //columns.set(12, "ddhmgsd");
        columns.set(2, "ddhm");
        columns.set(4, "kssj");
        columns.set(5, "jssj");
        columns.set(6, "thsc");
        columns.set(3, "hjlx");
        columns.set(0, "thlx");
        setHuadanList(start,huadanInfo,datalist,columns);
        return huadanInfo;
    }

    private void setHuadanList(int start, HuadanInfo huadanInfo,List<List<String>> datalist, List<String> columns) {
        List<HuadanList> huadanLists = new ArrayList<>();
        for (int i = start; i < datalist.size(); i++) {
            List<String> row = datalist.get(i);
            if(StringUtils.isBlank(row.get(0)) || row.get(0).startsWith("上网详单")){
                break;
            }
            if(row.get(0).startsWith("业务名称")){
                continue;
            }
            JSONObject data = new JSONObject();
            for (int j = 0; j < columns.size(); j++) {
                if (StringUtils.isNotBlank(columns.get(j)) && row.size()>j) {
                    Object value = row.get(j);
                    String key = columns.get(j);
                    //m没有添加过，或者为空，添加新的
                    if(!data.containsKey(key)
                            || StringUtils.isBlank(data.getString(key))){
                        if("thsc".equals(key) && value instanceof String && StringUtils.isNotBlank((String)value)){
                            String str = value.toString();
                            long sc = 0;
                            if(str.indexOf("时")>0){
                                String[] h = str.split("时");
                                sc += Long.valueOf(h[0])*60*60;
                                str = h[1];
                            }
                            if(str.indexOf("分")>0){
                                String[] h = str.split("分");
                                sc += Long.valueOf(h[0])*60;
                                str = h[1];
                            }
                            str = str.replace("秒","");
                            sc += Long.valueOf(str);
                            value = Double.valueOf(sc).longValue();
                        }else if("ddhm".equals(key) && value instanceof String && StringUtils.isNotBlank((String)value)){
                            value = value.toString().replace("0086|\\+86","");
                        }
                        data.put(columns.get(j), value);
                    }
                }
            }

            data.put("yys",huadanInfo.getYys());
            data.put("susp_id",huadanInfo.getSuspId());
            data.put("susp_name",huadanInfo.getSuspName());
            data.put("file_id",huadanInfo.getFileId());
            data.put("tags",huadanInfo.getTags());
            data.put("zjhm",huadanInfo.getZjhm());
            huadanLists.add(JSON.toJavaObject(data, HuadanList.class));
        }
        huadanInfo.setSize(huadanLists.size());
        huadanInfo.setHuadanLists(huadanLists);
    }

    private long splitSecond(String str) {
        String[] infos = str.split(":");
        long second = 0;
        if (infos.length == 3) {
            int hour = Integer.valueOf(infos[0]);
            int minutes = Integer.valueOf(infos[1]);
            int second_ = Integer.valueOf(infos[2]);
            second = hour * HOUR_SECOND + minutes * MINUTE_SECOND + second_;
        }
        return second;
    }

//    public static void main(String[] args) {
//        Attachment attachment = new Attachment();
//        HuadanParser parser = new HuadanParser("1");
//        try {
//            attachment.setFolder("联通");
//            parser.parser(attachment,"C:\\Users\\Administrator\\Desktop\\数据管理系统\\导入数据\\样本勿删20180402\\联通话单\\13025461418.xls");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
