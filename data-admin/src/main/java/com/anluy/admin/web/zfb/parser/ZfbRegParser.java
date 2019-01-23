package com.anluy.admin.web.zfb.parser;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.ZfbRegInfo;
import com.anluy.admin.entity.ZfbRegInfo;
import com.anluy.admin.utils.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能说明：支付宝文件解析
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class ZfbRegParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZfbRegParser.class);
    private final Attachment attachment;

    public ZfbRegParser(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<ZfbRegInfo>  parser(File file) throws Exception {
        CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(file),"GBK"));
        List<List<String>> stringList = csvReader.readAll();
        csvReader.close();
        return parse(stringList);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<ZfbRegInfo>  parser(String path) throws Exception {
        return parser(new File(path));
    }

    private List<ZfbRegInfo>  parse(List<List<String>> txtContent) {
        if(txtContent.size()<2){
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        List<ZfbRegInfo> dataList = new ArrayList<>();
        for (int i = 1; i < txtContent.size(); i++) {
            List<String> infolist = txtContent.get(i);
            if(infolist.size() < 10){
                throw new RuntimeException("文件格式不正确，不能解析");
            }
            ZfbRegInfo regInfo = new ZfbRegInfo();
            regInfo.setFileId(attachment.getId());
            regInfo.setTags(attachment.getTags());
            regInfo.setSuspId(attachment.getSuspId());
            regInfo.setSuspName(attachment.getSuspName());
            regInfo.setUserId(infolist.get(0));
            regInfo.setEmail(infolist.get(1));
            regInfo.setDlsj(infolist.get(2));
            regInfo.setName(infolist.get(3));
            regInfo.setZjlx(infolist.get(4));
            regInfo.setSfzh(infolist.get(5));
            if(StringUtils.isNotBlank(infolist.get(6))){
                regInfo.setYe(Double.valueOf(infolist.get(6)));
            }
            regInfo.setBdsj(infolist.get(7));

            List<String> kyhList = new ArrayList<>();
            List<String> yhzhList = new ArrayList<>();

            regInfo.setKhxxList(kyhList);
            regInfo.setYhzhList(yhzhList);

            if(StringUtils.isNotBlank(infolist.get(8))){
                String[] strs = infolist.get(8).split(";");
                for (String  infos :     strs            ) {
                    String[] s = infos.split(":");
                    if(s.length==3){
                        kyhList.add(s[1].trim()+":"+s[0].trim());
                        yhzhList.add(s[2].trim());
                    }
                }
            }
            regInfo.setXcbh(infolist.get(9));
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
        ZfbRegParser zfbRegParser = new ZfbRegParser(new Attachment());
        try {
            List<ZfbRegInfo> info = zfbRegParser.parser("C:\\Users\\Administrator\\Desktop\\数据管理系统\\导入数据\\样本勿删20180402\\支付宝模板\\注册信息.csv");
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
