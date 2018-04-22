package com.anluy.admin.web.zfb.parser;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.ZfbZzInfo;
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
 * 功能说明：支付宝转账明细文件解析
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class ZfbZzParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZfbZzParser.class);
    private final Attachment attachment;

    public ZfbZzParser(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<ZfbZzInfo> parser( File file) throws Exception {
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
    public List<ZfbZzInfo> parser(String path) throws Exception {
        return parser(new File(path));
    }

    private List<ZfbZzInfo> parse(List<String> txtContent) throws Exception{
        if(txtContent.size()<2){
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        List<ZfbZzInfo> dataList = new ArrayList<>();
        for (int i = 1; i < txtContent.size(); i++) {
            String line = txtContent.get(i);
            List<String> list = split( line);
            if(list.size() < 10){
                continue;
            }
            ZfbZzInfo regInfo = new ZfbZzInfo();
            regInfo.setFileId(attachment.getId());
            regInfo.setTags(attachment.getTags());
            regInfo.setJyh(list.get(0));
            regInfo.setFkfId(list.get(1));
            regInfo.setSkfId(list.get(2));
            regInfo.setSkjgmc(list.get(3));
            if(StringUtils.isNotBlank(list.get(4))){
                if(list.get(4).indexOf("/")>0){
                    regInfo.setDzsj(sdf2.parse(list.get(4)));
                }else {
                    regInfo.setDzsj(sdf1.parse(list.get(4)));
                }
            }
            if(StringUtils.isNotBlank(list.get(5))){
                regInfo.setJe(Double.valueOf(list.get(5)));
            }
            regInfo.setZzcpmc(list.get(6));
            regInfo.setJyfsd(list.get(7));
            regInfo.setTxlsh(list.get(8));
            regInfo.setXcbh(list.get(9));
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
        ZfbZzParser zfbRegParser = new ZfbZzParser(new Attachment());
        try {
            List<ZfbZzInfo> info = zfbRegParser.parser("C:\\Users\\Administrator\\Desktop\\数据管理系统\\导入数据\\样本勿删20180402\\支付宝模板\\转账明细.csv");
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
