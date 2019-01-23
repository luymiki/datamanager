package com.anluy.admin.web.yhzh.parser;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.YhzhKhxxInfo;
import com.anluy.admin.entity.ZfbJyjlInfo;
import com.anluy.admin.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateParser;
import org.apache.commons.lang3.time.FastDateParser;
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
 * 功能说明：银行账号开户信息文件解析
 * <p>
 * Created by hc.zeng on 2018/6/25.
 */
public class YhzhKhxxParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(YhzhKhxxParser.class);
    private final Attachment attachment;

    public YhzhKhxxParser(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<YhzhKhxxInfo> parser(String ssyh, File file) throws Exception {
        List<List<String>> list = ExcelUtils.read(file);
        return parse(ssyh, list);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<YhzhKhxxInfo> parser(String ssyh, String path) throws Exception {
        return parser(ssyh, new File(path));
    }

    private List<YhzhKhxxInfo> parse(String ssyh, List<List<String>> txtContent) throws Exception {
        if (txtContent.size() < 2) {
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
        List<YhzhKhxxInfo> dataList = new ArrayList<>();
        for (int i = 1; i < txtContent.size(); i++) {
            List<String> list = txtContent.get(i);
            if (list.size() < 7) {
                continue;
            }
            String kzh = list.get(6);
            if (StringUtils.isBlank(kzh)) {
                continue;
            }
            //工商——— 卡号:6220000000000737968;账号:4000020000008003876;开户日期:20090315;销户日期:20180301;开户网点:工行广东省深圳上步支行营业部;
            //建设——— 卡号:6224634601534634673;账号:62170034634634634;开户日期:20121104;销户日期:;开户网点:中国建设银行股份有限公司深圳翠园支行;卡号6224634601534634673;账号6224634601534634673;开户日期:20110904;销户日期:;开户网点:中国建设银行股份有限公司深圳翠园支行;
            //交通——— 卡号:6222600010006007540;账号6222600010006007540;开户日期:20091118;销户日期:;开户网点:交通银行深圳深南中支行;
            //农行——— 卡号:6228460120001530617;开户日期:20080829;销户日期:;开户网点:中国农业银行股份有限公司深圳万象城支行;
            String[] kzhs = kzh.split("卡号");
            for (String kzhstr : kzhs) {
                if(StringUtils.isBlank(kzhstr)){
                    continue;
                }
                String[] kzhInfostr = kzhstr.split(";|；");
                String kh = kzhInfostr[0];
                String zh = null;
                String khwd = null;
                String khrq = null;
                String xhrq = null;
                kh = kh.replace(":","").replace("：","");
                for (int j = 1; j < kzhInfostr.length; j++) {
                    String zhstr = kzhInfostr[j];
                    if(zhstr.startsWith("账号")){
                        zh = zhstr.replace("账号","").replace(":","").replace("：","");
                    }else if(zhstr.startsWith("开户日期")){
                        khrq = zhstr.replace("开户日期","").replace(":","").replace("：","");
                    }else if(zhstr.startsWith("销户日期")){
                        xhrq = zhstr.replace("销户日期","").replace(":","").replace("：","");
                    }else if(zhstr.startsWith("开户网点")){
                        khwd = zhstr.replace("开户网点","").replace(":","").replace("：","");
                    }
                }

                YhzhKhxxInfo khxx = new YhzhKhxxInfo();
                khxx.setFileId(attachment.getId());
                khxx.setTags(attachment.getTags());
                khxx.setSuspId(attachment.getSuspId());
                khxx.setSuspName(attachment.getSuspName());
                khxx.setSsyh(ssyh);
                khxx.setXh(list.get(0));
                khxx.setCxfkjg(list.get(1));
                khxx.setZzlx(list.get(2));
                khxx.setZzhm(list.get(3));
                khxx.setName(list.get(4));
                khxx.setLxdh(list.get(5));
                khxx.setKzhinfo(list.get(6));
                khxx.setKh(kh);
                khxx.setZh(zh);
                if(StringUtils.isNotBlank(khrq)){
                    try{
                        khxx.setKhrq(sdf3.parse(khrq));
                    }catch (Exception e){
                        LOGGER.error("解析开户日期失败"+e.getMessage(),e);
                    }
                }
                if(StringUtils.isNotBlank(xhrq)){
                    try{
                        khxx.setXhrq(sdf3.parse(xhrq));
                    }catch (Exception e){
                        LOGGER.error("解析销户日期失败"+e.getMessage(),e);
                    }
                }
                khxx.setKhwd(khwd);
                khxx.setZh(zh);
                khxx.setZh(zh);
                khxx.setLxsj(list.size()>7? list.get(7):null);
                khxx.setDbrxm(list.size()> 8 ? list.get(8):null);
                khxx.setDbrzjlx(list.size()>9 ? list.get(9):null);
                khxx.setDbrzjhm(list.size()>10? list.get(10):null);
                khxx.setZzdz(list.size()>11? list.get(11):null);
                khxx.setZzdh(list.size()>12? list.get(12):null);
                khxx.setGzdw(list.size()>13? list.get(13):null);
                khxx.setDwdz(list.size()>14? list.get(14):null);
                khxx.setDwdh(list.size()>15? list.get(15):null);
                khxx.setEmail(list.size()>16? list.get(16):null);
                khxx.setFrdb(list.size()>17? list.get(17):null);
                khxx.setFrdbzjlx(list.size()>18? list.get(18):null);
                khxx.setFrdbzjhm(list.size()>19? list.get(19):null);
                khxx.setKhgszzhm(list.size()>20? list.get(20):null);
                khxx.setGsnsh(list.size()>21? list.get(21):null);
                khxx.setDsnsh(list.size()>22? list.get(22):null);
                khxx.setDbrlxdh(list.size()>23? list.get(23):null);
                khxx.setWyzhm(list.size()>24? list.get(24):null);
                if (list.size()> 25 && StringUtils.isNotBlank(list.get(25))) {
                    if (list.get(25).indexOf("/") > 0) {
                        khxx.setWyblrq(sdf2.parse(list.get(25)));
                    } else {
                        khxx.setWyblrq(sdf1.parse(list.get(25)));
                    }
                }
                khxx.setWykhwd(list.size()>26? list.get(26):null);
                khxx.setWykhwddm(list.size()>27? list.get(27):null);
                khxx.setWykhwdszd(list.size()>28? list.get(28):null);
                khxx.setSjyhzhm(list.size()>29? list.get(29):null);
                if (list.size()>30 && StringUtils.isNotBlank(list.get(30))) {
                    if (list.get(30).indexOf("/") > 0) {
                        khxx.setSjyhblrq(sdf2.parse(list.get(30)));
                    } else {
                        khxx.setSjyhblrq(sdf1.parse(list.get(30)));
                    }
                }
                khxx.setSjyhkhwd(list.size()>31? list.get(31):null);
                khxx.setSjyhkhwddm(list.size()>32? list.get(32):null);
                khxx.setSjyhkhwdszd(list.size()>33? list.get(33):null);
                dataList.add(khxx);
            }
        }
        return dataList;
    }

    private List<String> split(String line) {
        String[] infos = line.split(",\t");
        List<String> hylist = new ArrayList<>();
        for (String hy : infos) {
            hylist.add(hy.replace("\"", "").trim());
        }
        return hylist;
    }

    public static void main(String[] args) {
        YhzhKhxxParser zfbRegParser = new YhzhKhxxParser(new Attachment());
        try {
            List<YhzhKhxxInfo> info = zfbRegParser.parser("工商银行", "C:\\Users\\Administrator\\Desktop\\数据管理系统\\数据导入20180614\\银行模板\\农行开户信息（7条）.xls");
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
