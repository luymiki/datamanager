package com.anluy.admin.web.zfb.parser;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.ZfbJyjlInfo;
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
 * 功能说明：支付宝交易记录文件解析
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class ZfbJyjlParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZfbJyjlParser.class);
    private final Attachment attachment;

    public ZfbJyjlParser(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<ZfbJyjlInfo> parser( File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
        String line = null;
        List<String> stringList = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            stringList.add(line);
        }
        return parse(stringList);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<ZfbJyjlInfo> parser(String path) throws Exception {
        return parser(new File(path));
    }

    private List<ZfbJyjlInfo> parse(List<String> txtContent) throws Exception{
        if(txtContent.size()<2){
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        List<ZfbJyjlInfo> dataList = new ArrayList<>();
        for (int i = 1; i < txtContent.size(); i++) {
            String line = txtContent.get(i);
            List<String> list = split( line);
            if(list.size() < 17){
                continue;
            }
            ZfbJyjlInfo regInfo = new ZfbJyjlInfo();
            regInfo.setFileId(attachment.getId());
            regInfo.setTags(attachment.getTags());
            regInfo.setJyh(list.get(0));
            regInfo.setWbjyh(list.get(1));
            regInfo.setJyzt(list.get(2));
            regInfo.setHzhbid(list.get(3));
            regInfo.setMjId(list.get(4));
            regInfo.setDsId(regInfo.getMjId());
            regInfo.setMjxx(list.get(5));
            regInfo.setMaijiaid(list.get(6));
            regInfo.setMaijiaxx(list.get(7));
            if(StringUtils.isNotBlank(list.get(8))){
                regInfo.setJe(Double.valueOf(list.get(8)));
                regInfo.setJyje(regInfo.getJe());
                if (regInfo.getJyje() != null) {
                    Double mod = regInfo.getJyje() % 100;
                    regInfo.setZc100(mod);
                } else {
                    regInfo.setZc100(-1.0);
                }
                regInfo.setJdlx("出");
            }
            if(StringUtils.isNotBlank(list.get(9))){
                if(list.get(9).indexOf("/")>0){
                    regInfo.setSksj(sdf2.parse(list.get(9)));
                }else {
                    regInfo.setSksj(sdf1.parse(list.get(9)));
                }
            }
            if(StringUtils.isNotBlank(list.get(10))){
                if(list.get(10).indexOf("/")>0){
                    regInfo.setZhxgsj(sdf2.parse(list.get(10)));
                }else {
                    regInfo.setZhxgsj(sdf1.parse(list.get(10)));
                }
            }
            if(StringUtils.isNotBlank(list.get(11))){
                if(list.get(11).indexOf("/")>0){
                    regInfo.setCjsj(sdf2.parse(list.get(11)));
                }else {
                    regInfo.setCjsj(sdf1.parse(list.get(11)));
                }
                if(regInfo.getCjsj()!=null){
                    regInfo.setJysj(regInfo.getCjsj());
                }
            }

            regInfo.setJylx(list.get(12));
            regInfo.setLyd(list.get(13));
            regInfo.setSpmc(list.get(14));
            regInfo.setShrdz(list.get(15));
            regInfo.setXcbh(list.get(16));
            dataList.add(regInfo);
        }
        return dataList;
    }

    private List<String> split(String line) {
        line = line.replace("o.,","o.，");
        line = line.replace(",\",","，\",");
        String[] infos = line.split(",");
        List<String> hylist = new ArrayList<>();
        for (String hy : infos) {
            hylist.add(hy.replace("\"","").replaceFirst("\t","").trim());
        }
        return hylist;
    }

    public static void main(String[] args) {
        ZfbJyjlParser zfbRegParser = new ZfbJyjlParser(new Attachment());
        try {
            List<ZfbJyjlInfo> info = zfbRegParser.parser("C:\\Users\\Administrator\\Desktop\\数据管理系统\\导入数据\\样本勿删20180402\\支付宝模板\\交易记录.csv");
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
