package com.anluy.admin.web.zfb.parser;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.ZfbLoginInfo;
import com.anluy.admin.entity.ZfbRegInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能说明：支付宝登录流水文件解析
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class ZfbLoginParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZfbLoginParser.class);
    private final Attachment attachment;

    public ZfbLoginParser(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<ZfbLoginInfo> parser( File file) throws Exception {
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
    public List<ZfbLoginInfo> parser(String path) throws Exception {
        return parser(new File(path));
    }

    private List<ZfbLoginInfo> parse(List<String> txtContent) throws Exception{
        if(txtContent.size()<2){
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ZfbLoginInfo> dataList = new ArrayList<>();
        for (int i = 1; i < txtContent.size(); i++) {
            String line = txtContent.get(i);
            List<String> list = split( line);
            if(list.size() < 6){
                continue;
            }
            ZfbLoginInfo regInfo = new ZfbLoginInfo();
            regInfo.setFileId(attachment.getId());
            regInfo.setTags(attachment.getTags());
           // regInfo.setInfoId(attachment.getFolder());//借用folder字段方注册信息的编号
            regInfo.setDlzh(list.get(0));
            regInfo.setUserId(list.get(1));
            regInfo.setName(list.get(2));
            regInfo.setIp(list.get(3));
            if(StringUtils.isNotBlank(list.get(4))){
                regInfo.setCzsj(sdf.parse(list.get(4)));
            }
            regInfo.setXcbh(list.get(5));
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
        ZfbLoginParser zfbRegParser = new ZfbLoginParser(new Attachment());
        try {
            List<ZfbLoginInfo> info = zfbRegParser.parser("C:\\Users\\Administrator\\Desktop\\数据管理系统\\导入数据\\样本勿删20180402\\支付宝模板\\登陆日志.csv");
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
