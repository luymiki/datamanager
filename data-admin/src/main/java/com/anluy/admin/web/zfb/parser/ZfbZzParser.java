package com.anluy.admin.web.zfb.parser;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.ZfbZzInfo;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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
    public List<ZfbZzInfo> parser(File file,Set<String> xcbhSet) throws Exception {
        CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(file),"GBK"));
        List<List<String>> stringList = csvReader.readAll();
        return parse(stringList,xcbhSet);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<ZfbZzInfo> parser(String path,Set<String> xcbhSet) throws Exception {
        return parser(new File(path),xcbhSet);
    }

    private List<ZfbZzInfo> parse(List<List<String>> txtContent, Set<String> xcbhSet) throws Exception {
        if (txtContent.size() < 2) {
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        List<ZfbZzInfo> dataList = new ArrayList<>();
        for (int i = 1; i < txtContent.size(); i++) {
            List<String> list = txtContent.get(i);
            if (list.size() < 10) {
                continue;
            }
            ZfbZzInfo regInfo = new ZfbZzInfo();
            regInfo.setFileId(attachment.getId());
            regInfo.setTags(attachment.getTags());
            regInfo.setJyh(list.get(0));
            regInfo.setFkfId(list.get(1));
//            regInfo.setUserId(regInfo.getFkfId());
            regInfo.setSkfId(list.get(2));
//            //如果是收款方账户id等于支付宝id说明是转入
//            if (regInfo.getSkfId() != null && regInfo.getSkfId().equals(regInfo.getUserId())) {
//                regInfo.setJdlx("出");
//                regInfo.setDsId(regInfo.getSkfId());
//            }else {
//                regInfo.setJdlx("入");
//                regInfo.setDsId(regInfo.getFkfId());
//                regInfo.setUserId(regInfo.getSkfId());
//            }
            regInfo.setSkjgmc(list.get(3));
            if (StringUtils.isNotBlank(list.get(4))) {
                if (list.get(4).indexOf("/") > 0) {
                    regInfo.setDzsj(sdf2.parse(list.get(4)));
                } else {
                    regInfo.setDzsj(sdf1.parse(list.get(4)));
                }
                if (regInfo.getDzsj() != null) {
                    regInfo.setJysj(regInfo.getDzsj());
                }
            }
            if (StringUtils.isNotBlank(list.get(5))) {
                regInfo.setJe(Double.valueOf(list.get(5)));
                regInfo.setJyje(regInfo.getJe());
                if (regInfo.getJyje() != null) {
                    Double mod = regInfo.getJyje() % 100;
                    regInfo.setZc100(mod);
                } else {
                    regInfo.setZc100(-1.0);
                }
            }
            regInfo.setZzcpmc(list.get(6));
            regInfo.setJyfsd(list.get(7));
            regInfo.setTxlsh(list.get(8));
            regInfo.setXcbh(list.get(9));
            dataList.add(regInfo);
            if(StringUtils.isNotBlank(regInfo.getXcbh())){
                xcbhSet.add(regInfo.getXcbh());
            }
        }
        return dataList;
    }

    private List<String> split(String line) {
        String[] infos = line.split(",");
        List<String> hylist = new ArrayList<>();
        for (String hy : infos) {
            hylist.add(hy.replace("\"", "").trim());
        }
        return hylist;
    }

    public static void main(String[] args) {
        ZfbZzParser zfbRegParser = new ZfbZzParser(new Attachment());
        try {
            List<ZfbZzInfo> info = zfbRegParser.parser("C:\\Users\\Administrator\\Desktop\\数据管理系统\\导入数据\\样本勿删20180402\\支付宝模板\\转账明细.csv",new HashSet<>());
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
