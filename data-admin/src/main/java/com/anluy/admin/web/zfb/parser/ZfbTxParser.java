package com.anluy.admin.web.zfb.parser;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.ZfbTxInfo;
import com.anluy.admin.utils.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能说明：支付宝提现记录文件解析
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class ZfbTxParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZfbTxParser.class);
    private final Attachment attachment;

    public ZfbTxParser(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<ZfbTxInfo> parser( File file) throws Exception {
        CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(file),"GBK"));
        List<List<String>> stringList = csvReader.readAll();
        return parse(stringList);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<ZfbTxInfo> parser(String path) throws Exception {
        return parser(new File(path));
    }

    private List<ZfbTxInfo> parse(List<List<String>> txtContent) throws Exception{
        if(txtContent.size()<2){
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        List<ZfbTxInfo> dataList = new ArrayList<>();
        for (int i = 1; i < txtContent.size(); i++) {
            List<String> list = txtContent.get(i);
            if(list.size() < 11){
                continue;
            }
            ZfbTxInfo regInfo = new ZfbTxInfo();
            regInfo.setFileId(attachment.getId());
            regInfo.setTags(attachment.getTags());
            regInfo.setTxlx(list.get(0));
            regInfo.setTxlsh(list.get(1));
            regInfo.setUserId(list.get(2));
            regInfo.setKhyh(list.get(3));
            regInfo.setYhzh(list.get(4));
            regInfo.setDsId(regInfo.getYhzh());
            if(StringUtils.isNotBlank(list.get(5))){
                if(list.get(5).indexOf("/")>0){
                    regInfo.setSqsj(sdf2.parse(list.get(5)));
                }else {
                    regInfo.setSqsj(sdf1.parse(list.get(5)));
                }
                if(regInfo.getSqsj()!=null){
                    regInfo.setJysj(regInfo.getSqsj());
                }
            }
            if(StringUtils.isNotBlank(list.get(6))){
                if(list.get(6).indexOf("/")>0){
                    regInfo.setClsj(sdf2.parse(list.get(6)));
                }else {
                    regInfo.setClsj(sdf1.parse(list.get(6)));
                }
            }
            if(StringUtils.isNotBlank(list.get(7))){
                regInfo.setJe(Double.valueOf(list.get(7)));
                regInfo.setJyje(regInfo.getJe());
                if (regInfo.getJyje() != null) {
                    Double mod = regInfo.getJyje() % 100;
                    regInfo.setZc100(mod);
                } else {
                    regInfo.setZc100(-1.0);
                }
                regInfo.setJdlx("出");
            }
            regInfo.setZt(list.get(8));
            regInfo.setSbyy(list.get(9));
            regInfo.setXcbh(list.get(10));
            dataList.add(regInfo);
        }
        return dataList;
    }

    private List<String> split(String line) {
        String[] infos = line.split(",");
        List<String> hylist = new ArrayList<>();
        for (String hy : infos) {
            hylist.add(hy.replace("\"","").trim());
        }
        return hylist;
    }

    public static void main(String[] args) {
        ZfbTxParser zfbRegParser = new ZfbTxParser(new Attachment());
        try {
            List<ZfbTxInfo> info = zfbRegParser.parser("C:\\Users\\Administrator\\Desktop\\数据管理系统\\导入数据\\样本勿删20180402\\支付宝模板\\提现记录.csv");
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
