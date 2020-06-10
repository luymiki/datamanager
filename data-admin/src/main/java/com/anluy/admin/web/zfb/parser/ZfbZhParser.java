package com.anluy.admin.web.zfb.parser;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.ZfbZhInfo;
import com.anluy.admin.utils.CSVReader;
import com.anluy.admin.utils.ExcelUtils;
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
 * 功能说明：支付宝账户明细文件解析
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class ZfbZhParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZfbZhParser.class);
    private final Attachment attachment;

    public ZfbZhParser(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<ZfbZhInfo> parser(File file) throws Exception {
        CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(file), "GBK"));
        List<List<String>> stringList = csvReader.readAll();
        csvReader.close();
        return parse(stringList);
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<ZfbZhInfo> parserExcel(File file, String userId, String dlzh, String name, String xcbh) throws Exception {
        List<List<String>> stringList = ExcelUtils.read(file, "账户明细");
        return parseExcel(stringList, userId, dlzh, name, xcbh);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<ZfbZhInfo> parser(String path) throws Exception {
        return parser(new File(path));
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<ZfbZhInfo> parserExcel(String path, String userId, String dlzh, String name, String xcbh) throws Exception {
        return parserExcel(new File(path), userId, dlzh, name, xcbh);
    }

    private List<ZfbZhInfo> parse(List<List<String>> txtContent) throws Exception {
        if (txtContent.size() < 2) {
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        List<ZfbZhInfo> dataList = new ArrayList<>();
        for (int i = 1; i < txtContent.size(); i++) {
            List<String> list = txtContent.get(i);
            if (list.size() < 15) {
                continue;
            }
            ZfbZhInfo regInfo = new ZfbZhInfo();
            regInfo.setFileId(attachment.getId());
            regInfo.setTags(attachment.getTags());
            regInfo.setJyh(list.get(0));
            regInfo.setShddh(list.get(1));
            if (StringUtils.isNotBlank(list.get(2))) {
                if (list.get(2).indexOf("/") > 0) {
                    regInfo.setJycjsj(sdf2.parse(list.get(2)));
                } else {
                    regInfo.setJycjsj(sdf1.parse(list.get(2)));
                }
                if (regInfo.getJycjsj() != null) {
                    regInfo.setJysj(regInfo.getJycjsj());
                }
            }
            if (StringUtils.isNotBlank(list.get(3))) {
                if (list.get(3).indexOf("/") > 0) {
                    regInfo.setFksj(sdf2.parse(list.get(3)));
                } else {
                    regInfo.setFksj(sdf1.parse(list.get(3)));
                }
            }
            if (StringUtils.isNotBlank(list.get(4))) {
                if (list.get(4).indexOf("/") > 0) {
                    regInfo.setZjxgsj(sdf2.parse(list.get(4)));
                } else {
                    regInfo.setZjxgsj(sdf1.parse(list.get(4)));
                }
            }
            regInfo.setJylyd(list.get(5));
            regInfo.setJylx(list.get(6));
            if (StringUtils.isNotBlank(list.get(7))) {
                String[] yhxx = list.get(7).split("\\(|\\)");
                regInfo.setUserId(yhxx[0]);
                regInfo.setName(yhxx[1]);
            }
            if (StringUtils.isNotBlank(list.get(8))) {
                String[] yhxx = list.get(8).split("\\(|\\)");
                if (yhxx.length > 0) {
                    regInfo.setDfUserId(yhxx[0]);
                    regInfo.setDsId(regInfo.getDfUserId());
                }
                if (yhxx.length > 1)
                    regInfo.setDfName(yhxx[1]);
            }
            regInfo.setXfmc(list.get(9));
            if (StringUtils.isNotBlank(list.get(10))) {
                regInfo.setJe(Double.valueOf(list.get(10)));
                regInfo.setJyje(regInfo.getJe());
                if (regInfo.getJyje() != null) {
                    Double mod = regInfo.getJyje() % 100;
                    regInfo.setZc100(mod);
                } else {
                    regInfo.setZc100(-1.0);
                }
            }
            regInfo.setSjbj(list.get(11));
            if (StringUtils.isNotBlank(regInfo.getSjbj())) {
                if ("支出".equals(regInfo.getSjbj()))
                    regInfo.setJdlx("出");
                else
                    regInfo.setJdlx("入");
            }

            regInfo.setJyzt(list.get(12));
            regInfo.setBz(list.get(13));
            regInfo.setXcbh(list.get(14));
            dataList.add(regInfo);
        }
        return dataList;
    }

    private List<ZfbZhInfo> parseExcel(List<List<String>> txtContent, String userId, String dlzh, String name, String xcbh) throws Exception {
        if (txtContent.size() < 2) {
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        List<ZfbZhInfo> dataList = new ArrayList<>();
        for (int i = 1; i < txtContent.size(); i++) {
            List<String> list = txtContent.get(i);
            if (list.size() < 20) {
                continue;
            }
            ZfbZhInfo regInfo = new ZfbZhInfo();
            regInfo.setFileId(attachment.getId());
            regInfo.setTags(attachment.getTags());
            regInfo.setJyh(list.get(13));
            regInfo.setShddh(list.get(14));
            if (StringUtils.isNotBlank(list.get(18))) {
                if (list.get(18).indexOf("/") > 0) {
                    regInfo.setJycjsj(sdf2.parse(list.get(18)));
                } else {
                    regInfo.setJycjsj(sdf1.parse(list.get(18)));
                }
                if (regInfo.getJycjsj() != null) {
                    regInfo.setJysj(regInfo.getJycjsj());
                }
            }
            regInfo.setFksj(regInfo.getJysj());
            if (StringUtils.isNotBlank(list.get(19))) {
                if (list.get(19).indexOf("/") > 0) {
                    regInfo.setZjxgsj(sdf2.parse(list.get(19)));
                } else {
                    regInfo.setZjxgsj(sdf1.parse(list.get(19)));
                }
            }
            regInfo.setJylyd(list.get(10));
            regInfo.setJylx(list.get(11));
            regInfo.setUserId(userId);
            regInfo.setName(name);

            if (StringUtils.isNotBlank(list.get(2))) {
                regInfo.setDfUserId(list.get(2));
                regInfo.setDfName(list.get(3));
            }else if(StringUtils.isNotBlank(list.get(3))){
                regInfo.setDfUserId(list.get(3));
                regInfo.setDfName(list.get(3));
            }
            regInfo.setDsId(regInfo.getDfUserId());

            regInfo.setXfmc(list.get(4));
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
            regInfo.setSjbj(list.get(9));
            if (StringUtils.isNotBlank(regInfo.getSjbj())) {
                if ("支出".equals(regInfo.getSjbj()))
                    regInfo.setJdlx("出");
                else
                    regInfo.setJdlx("入");
            }

            regInfo.setJyzt(list.get(17));
            regInfo.setBz(list.get(15));
            regInfo.setXcbh(xcbh);
            dataList.add(regInfo);
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
        ZfbZhParser zfbRegParser = new ZfbZhParser(new Attachment());
        try {
            List<ZfbZhInfo> info = zfbRegParser.parser("C:\\Users\\Administrator\\Desktop\\数据管理系统\\导入数据\\样本勿删20180402\\支付宝模板\\账户明细.csv");
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
