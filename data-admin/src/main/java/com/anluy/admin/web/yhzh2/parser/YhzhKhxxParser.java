package com.anluy.admin.web.yhzh2.parser;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.YhzhKhxxInfo;
import com.anluy.admin.utils.ClassUtils;
import com.anluy.admin.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

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
    public List<YhzhKhxxInfo> parser(String ssyh, File file, String sheetName) throws Exception {
        List<List<String>> list = ExcelUtils.read(file, sheetName);
        return parse(ssyh, list);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<YhzhKhxxInfo> parser(String ssyh, String path, String sheetName) throws Exception {
        return parser(ssyh, new File(path), sheetName);
    }

    private List<YhzhKhxxInfo> parse(String ssyh, List<List<String>> txtContent) throws Exception {
        if (txtContent.size() < 2) {
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        Map<Integer,ClassUtils.ClassFieldInfo> titleMapping = null;
        Map<Integer,ClassUtils.ClassFieldInfo> subTitleMapping = null;
        List<String> titleList = null;
        List<String> subTitleList = null;
        Map<String,Function> functionMap = new HashMap<>();
        functionMap.put("网银办理日期",ClassUtils.FUNCTION_DATE_YYYYMMDDHHMMSS_GG_X);
        functionMap.put("手机银行办理日期",ClassUtils.FUNCTION_DATE_YYYYMMDDHHMMSS_GG_X);
        functionMap.put("开户日期",ClassUtils.FUNCTION_DATE_YYYYMMDD);
        functionMap.put("销户日期",ClassUtils.FUNCTION_DATE_YYYYMMDD);

        List<YhzhKhxxInfo> dataList = new ArrayList<>();
        List<String> baseList = null;
        for (int i = 0; i < txtContent.size(); i++) {
            List<String> list = txtContent.get(i);
            //如果整行为空，跳过
            if (blankList(list)) {
                baseList = null;
                continue;
            }

            if ("开户银行".equals(list.get(0))) {
                if(titleList ==null || titleList.isEmpty()){
                    titleList = txtContent.get(i);
                    titleMapping = ClassUtils.loadIndexMapping(YhzhKhxxInfo.class,titleList);
                }
                i++;
                baseList = txtContent.get(i);
                continue;
            } else if ("账号信息".equals(list.get(2))) {
                if(subTitleList ==null || subTitleList.isEmpty()){
                    subTitleList = txtContent.get(i +1);
                    subTitleMapping = ClassUtils.loadIndexMapping(YhzhKhxxInfo.class,subTitleList);
                }
                i += 2;
                list = txtContent.get(i);
                if (baseList == null) {
                    continue;
                }

            }else if ("关联子账户信息".equals(list.get(2))) {
                i += 2;
                baseList = null;
                continue;
            }


            if (baseList != null) {
                String kh  = list.size() > 2 ? list.get(2) : null;
                String zh = list.size() > 3 ? list.get(3) : null;
                if(StringUtils.isBlank(kh) && StringUtils.isBlank(zh)){
                    continue;
                }
                YhzhKhxxInfo khxx = new YhzhKhxxInfo();
                khxx.setFileId(attachment.getId());
                khxx.setTags(attachment.getTags());
                khxx.setSsyh(ssyh);

                ClassUtils.setDataToObject(khxx,baseList,titleMapping,functionMap);
                ClassUtils.setDataToObject(khxx,list,subTitleMapping,functionMap);

//                khxx.setCxfkjg(baseList.get(1));
//                khxx.setZzlx(baseList.get(2));
//                khxx.setZzhm(baseList.get(3));
//                khxx.setName(baseList.get(4));
//                khxx.setLxdh(baseList.get(5));
//                khxx.setLxsj(baseList.get(6));
//                khxx.setDbrxm(baseList.size() > 7 ? baseList.get(7) : null);
//                khxx.setDbrzjlx(baseList.size() > 8 ? baseList.get(8) : null);
//                khxx.setDbrzjhm(baseList.size() > 9 ? baseList.get(9) : null);
//                khxx.setZzdz(baseList.size() > 10 ? baseList.get(10) : null);
//                khxx.setZzdh(baseList.size() > 11 ? baseList.get(11) : null);
//                khxx.setGzdw(baseList.size() > 12 ? baseList.get(12) : null);
//                khxx.setDwdz(baseList.size() > 13 ? baseList.get(13) : null);
//                khxx.setDwdh(baseList.size() > 14 ? baseList.get(14) : null);
//                khxx.setEmail(baseList.size() > 15 ? baseList.get(15) : null);
//                khxx.setFrdb(baseList.size() > 16 ? baseList.get(16) : null);
//                khxx.setFrdbzjlx(baseList.size() > 17 ? baseList.get(17) : null);
//                khxx.setFrdbzjhm(baseList.size() > 18 ? baseList.get(18) : null);
//                khxx.setKhgszzhm(baseList.size() > 19 ? baseList.get(19) : null);
//                khxx.setGsnsh(baseList.size() > 20 ? baseList.get(20) : null);
//                khxx.setDsnsh(baseList.size() > 21 ? baseList.get(21) : null);
//                khxx.setDbrlxdh(baseList.size() > 22 ? baseList.get(22) : null);
//                khxx.setWyzhm(baseList.size() > 23 ? baseList.get(23) : null);
//                if (baseList.size() > 24 && StringUtils.isNotBlank(baseList.get(24))) {
//                    if (baseList.get(24).indexOf("/") > 0) {
//                        khxx.setWyblrq(sdf2.parse(baseList.get(24)));
//                    } else {
//                        khxx.setWyblrq(sdf1.parse(baseList.get(24)));
//                    }
//                }
//                khxx.setWykhwd(baseList.size() > 25 ? baseList.get(25) : null);
//                khxx.setWykhwddm(baseList.size() > 26 ? baseList.get(26) : null);
//                khxx.setWykhwdszd(baseList.size() > 27 ? baseList.get(27) : null);
//                khxx.setSjyhzhm(baseList.size() > 28 ? baseList.get(28) : null);
//                if (baseList.size() > 29 && StringUtils.isNotBlank(baseList.get(29))) {
//                    if (baseList.get(29).indexOf("/") > 0) {
//                        khxx.setSjyhblrq(sdf2.parse(baseList.get(29)));
//                    } else {
//                        khxx.setSjyhblrq(sdf1.parse(baseList.get(29)));
//                    }
//                }
//                khxx.setSjyhkhwd(baseList.size() > 30 ? baseList.get(30) : null);
//                khxx.setSjyhkhwddm(baseList.size() > 31 ? baseList.get(31) : null);
//                khxx.setSjyhkhwdszd(baseList.size() > 32 ? baseList.get(32) : null);
//                khxx.setKzhinfo(baseList.size() > 33 ? baseList.get(33) : null);
//
//
//                khxx.setKh(list.size() > 2 ? list.get(2) : null);
//                khxx.setZh(list.size() > 3 ? list.get(3) : null);
//                String khrq = list.size() > 8 ? list.get(8) : null;
//                if (StringUtils.isNotBlank(khrq)) {
//                    try {
//                        khxx.setKhrq(sdf3.parse(khrq));
//                    } catch (Exception e) {
//                        LOGGER.error("解析开户日期失败" + e.getMessage(), e);
//                    }
//                }
//                String xhrq = list.size() > 9 ? list.get(9) : null;
//                if (StringUtils.isNotBlank(xhrq)) {
//                    try {
//                        khxx.setXhrq(sdf3.parse(xhrq));
//                    } catch (Exception e) {
//                        LOGGER.error("解析销户日期失败" + e.getMessage(), e);
//                    }
//                }
//                khxx.setKhwd(list.size() > 6 ? list.get(6) : null);


                dataList.add(khxx);

            }


        }
//        for (int i = 1; i < txtContent.size(); i++) {
//            List<String> list = txtContent.get(i);
//            if (list.size() < 7) {
//                continue;
//            }
//            String kzh = list.get(6);
//            if (StringUtils.isBlank(kzh)) {
//                continue;
//            }
//            //工商——— 卡号:6220000000000737968;账号:4000020000008003876;开户日期:20090315;销户日期:20180301;开户网点:工行广东省深圳上步支行营业部;
//            //建设——— 卡号:6224634601534634673;账号:62170034634634634;开户日期:20121104;销户日期:;开户网点:中国建设银行股份有限公司深圳翠园支行;卡号6224634601534634673;账号6224634601534634673;开户日期:20110904;销户日期:;开户网点:中国建设银行股份有限公司深圳翠园支行;
//            //交通——— 卡号:6222600010006007540;账号6222600010006007540;开户日期:20091118;销户日期:;开户网点:交通银行深圳深南中支行;
//            //农行——— 卡号:6228460120001530617;开户日期:20080829;销户日期:;开户网点:中国农业银行股份有限公司深圳万象城支行;
//            String[] kzhs = kzh.split("卡号");
//            for (String kzhstr : kzhs) {
//                if(StringUtils.isBlank(kzhstr)){
//                    continue;
//                }
//                String[] kzhInfostr = kzhstr.split(";|；");
//                String kh = kzhInfostr[0];
//                String zh = null;
//                String khwd = null;
//                String khrq = null;
//                String xhrq = null;
//                kh = kh.replace(":","").replace("：","");
//                for (int j = 1; j < kzhInfostr.length; j++) {
//                    String zhstr = kzhInfostr[j];
//                    if(zhstr.startsWith("账号")){
//                        zh = zhstr.replace("账号","").replace(":","").replace("：","");
//                    }else if(zhstr.startsWith("开户日期")){
//                        khrq = zhstr.replace("开户日期","").replace(":","").replace("：","");
//                    }else if(zhstr.startsWith("销户日期")){
//                        xhrq = zhstr.replace("销户日期","").replace(":","").replace("：","");
//                    }else if(zhstr.startsWith("开户网点")){
//                        khwd = zhstr.replace("开户网点","").replace(":","").replace("：","");
//                    }
//                }
//
//                YhzhKhxxInfo khxx = new YhzhKhxxInfo();
//                khxx.setFileId(attachment.getId());
//                khxx.setTags(attachment.getTags());
//                khxx.setSsyh(ssyh);
//                khxx.setXh(list.get(0));
//                khxx.setCxfkjg(list.get(1));
//                khxx.setZzlx(list.get(2));
//                khxx.setZzhm(list.get(3));
//                khxx.setName(list.get(4));
//                khxx.setLxdh(list.get(5));
//                khxx.setKzhinfo(list.get(6));
//                khxx.setKh(kh);
//                khxx.setZh(zh);
//                if(StringUtils.isNotBlank(khrq)){
//                    try{
//                        khxx.setKhrq(sdf3.parse(khrq));
//                    }catch (Exception e){
//                        LOGGER.error("解析开户日期失败"+e.getMessage(),e);
//                    }
//                }
//                if(StringUtils.isNotBlank(xhrq)){
//                    try{
//                        khxx.setXhrq(sdf3.parse(xhrq));
//                    }catch (Exception e){
//                        LOGGER.error("解析销户日期失败"+e.getMessage(),e);
//                    }
//                }
//                khxx.setKhwd(khwd);
//                khxx.setZh(zh);
//                khxx.setZh(zh);
//                khxx.setLxsj(list.size()>7? list.get(7):null);
//                khxx.setDbrxm(list.size()> 8 ? list.get(8):null);
//                khxx.setDbrzjlx(list.size()>9 ? list.get(9):null);
//                khxx.setDbrzjhm(list.size()>10? list.get(10):null);
//                khxx.setZzdz(list.size()>11? list.get(11):null);
//                khxx.setZzdh(list.size()>12? list.get(12):null);
//                khxx.setGzdw(list.size()>13? list.get(13):null);
//                khxx.setDwdz(list.size()>14? list.get(14):null);
//                khxx.setDwdh(list.size()>15? list.get(15):null);
//                khxx.setEmail(list.size()>16? list.get(16):null);
//                khxx.setFrdb(list.size()>17? list.get(17):null);
//                khxx.setFrdbzjlx(list.size()>18? list.get(18):null);
//                khxx.setFrdbzjhm(list.size()>19? list.get(19):null);
//                khxx.setKhgszzhm(list.size()>20? list.get(20):null);
//                khxx.setGsnsh(list.size()>21? list.get(21):null);
//                khxx.setDsnsh(list.size()>22? list.get(22):null);
//                khxx.setDbrlxdh(list.size()>23? list.get(23):null);
//                khxx.setWyzhm(list.size()>24? list.get(24):null);
//                if (list.size()> 25 && StringUtils.isNotBlank(list.get(25))) {
//                    if (list.get(25).indexOf("/") > 0) {
//                        khxx.setWyblrq(sdf2.parse(list.get(25)));
//                    } else {
//                        khxx.setWyblrq(sdf1.parse(list.get(25)));
//                    }
//                }
//                khxx.setWykhwd(list.size()>26? list.get(26):null);
//                khxx.setWykhwddm(list.size()>27? list.get(27):null);
//                khxx.setWykhwdszd(list.size()>28? list.get(28):null);
//                khxx.setSjyhzhm(list.size()>29? list.get(29):null);
//                if (list.size()>30 && StringUtils.isNotBlank(list.get(30))) {
//                    if (list.get(30).indexOf("/") > 0) {
//                        khxx.setSjyhblrq(sdf2.parse(list.get(30)));
//                    } else {
//                        khxx.setSjyhblrq(sdf1.parse(list.get(30)));
//                    }
//                }
//                khxx.setSjyhkhwd(list.size()>31? list.get(31):null);
//                khxx.setSjyhkhwddm(list.size()>32? list.get(32):null);
//                khxx.setSjyhkhwdszd(list.size()>33? list.get(33):null);
//                dataList.add(khxx);
//            }
//        }
        return dataList;
    }
    private boolean blankList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        for (String str : list) {
            if (StringUtils.isNotBlank(str)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        YhzhKhxxParser zfbRegParser = new YhzhKhxxParser(new Attachment());
        try {
            List<YhzhKhxxInfo> info = zfbRegParser.parser("工商银行", "H:\\数据管理系统\\数据导入20180903\\工行客户及交易信息（9-20，1516条）.xls", "客户信息");
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
