package com.anluy.admin.web.yhzh.parser;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.YhzhJylsInfo;
import com.anluy.admin.utils.ClassUtils;
import com.anluy.admin.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 功能说明：银行账号交易流水信息文件解析
 * <p>
 * Created by hc.zeng on 2018/6/25.
 */
public class YhzhJylsParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(YhzhJylsParser.class);
    private final Attachment attachment;

    public YhzhJylsParser(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<YhzhJylsInfo> parser(String ssyh, File file) throws Exception {
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
    public List<YhzhJylsInfo> parser(String ssyh, String path) throws Exception {
        return parser(ssyh, new File(path));
    }

    private List<YhzhJylsInfo> parse(String ssyh, List<List<String>> txtContent) throws Exception {
        if (txtContent.size() < 2) {
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        List<String> titleList = txtContent.get(0);
        Map<Integer,ClassUtils.ClassFieldInfo> titleMapping = ClassUtils.loadIndexMapping(YhzhJylsInfo.class,titleList);
        Map<String,Function> functionMap = new HashMap<>();
        functionMap.put("交易金额",ClassUtils.FUNCTION_DOUBLE);
        functionMap.put("交易余额",ClassUtils.FUNCTION_DOUBLE);
        functionMap.put("交易时间",ClassUtils.FUNCTION_DATE_YYYYMMDDHHMMSS);
        functionMap.put("交易手续费金额",ClassUtils.FUNCTION_DOUBLE);

        List<YhzhJylsInfo> dataList = new ArrayList<>();
        for (int i = 1; i < txtContent.size(); i++) {
            List<String> list = txtContent.get(i);
            if (list.size() < 11) {
                continue;
            }
            YhzhJylsInfo jyls = new YhzhJylsInfo();
            jyls.setFileId(attachment.getId());
            jyls.setTags(attachment.getTags());
            jyls.setSuspId(attachment.getSuspId());
            jyls.setSuspName(attachment.getSuspName());
            jyls.setSsyh(ssyh);
            ClassUtils.setDataToObject(jyls,list,titleMapping,functionMap);
            if (jyls.getJyje()!=null) {
                if(jyls.getJyje()<0){
                    jyls.setJyje(jyls.getJyje()*-1);
                }
                jyls.setZc100(jyls.getJyje() % 100);
            }
            if (StringUtils.isNotBlank(jyls.getDfkh())) {
                jyls.setDsId(jyls.getDfkh());
            } else if (StringUtils.isNotBlank(jyls.getDfzh())) {
                jyls.setDsId(jyls.getDfzh());
            } else {
                jyls.setDsId("-");
            }

//            jyls.setXh(list.get(0));
//            jyls.setCxfkjg(list.get(1));
//            jyls.setCxfkjgyy(list.get(2));
//            jyls.setKh(list.get(3));
//            jyls.setZh(list.get(4));
//            jyls.setJylx(list.get(5));
//            jyls.setJdlx(list.get(6));
//            jyls.setJybz(list.get(7));
//            if (StringUtils.isNotBlank(list.get(8))) {
//                jyls.setJyje(Double.valueOf(list.get(8)));
//                if(jyls.getJyje()<0){
//                    jyls.setJyje(jyls.getJyje()*-1);
//                }
//                jyls.setZc100(jyls.getJyje()%100);
//            }
//            if (StringUtils.isNotBlank(list.get(9))) {
//                jyls.setJyye(Double.valueOf(list.get(9)));
//            }
//            if (StringUtils.isNotBlank(list.get(10))) {
//                jyls.setJysj(sdf1.parse(list.get(10)));
//            }
//            jyls.setJylsh(list.size()>11? list.get(11):null);
//            jyls.setDfmc(list.size()>12? list.get(12):null);
//            jyls.setDfzh(list.size()>13? list.get(13):null);
//            jyls.setDfkh(list.size()>14? list.get(14):null);
//            if (StringUtils.isNotBlank(jyls.getDfkh())) {
//                jyls.setDsId(jyls.getDfkh());
//            } else if (StringUtils.isNotBlank(jyls.getDfzh())) {
//                jyls.setDsId(jyls.getDfzh());
//            }else{
//                jyls.setDsId("-");
//            }
//
//            jyls.setDfzjhm(list.size()>15? list.get(15):null);
//            jyls.setDfye(list.size()>16? list.get(16):null);
//            jyls.setDfzhkhh(list.size()>17? list.get(17):null);
//            jyls.setJyzy(list.size()>18? list.get(18):null);
//            jyls.setJywdmc(list.size()>19? list.get(19):null);
//            jyls.setJywddm(list.size()>20? list.get(20):null);
//            jyls.setRzh(list.size()>21? list.get(21):null);
//            jyls.setCph(list.size()>22? list.get(22):null);
//            jyls.setPzzl(list.size()>23? list.get(23):null);
//            jyls.setPzh(list.size()>24? list.get(24):null);
//            jyls.setXjbz(list.size()>25? list.get(25):null);
//            jyls.setZdh(list.size()>26? list.get(26):null);
//            jyls.setJysfcg(list.size()>27? list.get(27):null);
//            jyls.setJyfsd(list.size()>28? list.get(28):null);
//            jyls.setShmc(list.size()>29? list.get(29):null);
//            jyls.setShh(list.size()>30? list.get(30):null);
//            jyls.setIp(list.size()>31? list.get(31):null);
//            jyls.setMac(list.size()>32? list.get(32):null);
//            jyls.setJygyh(list.size()>33? list.get(33):null);
//            jyls.setBz(list.size()>34? list.get(34):null);
//            jyls.setJyssfbz(list.size()>35? list.get(35):null);
//            if (list.size()>  36 && StringUtils.isNotBlank(list.get(36))) {
//                jyls.setJyssfje(Double.valueOf(list.get(36)));
//            }
//            jyls.setJyssfzy(list.size()>37? list.get(37):null);
//            jyls.setDlyhm(list.size()>38? list.get(38):null);
//            jyls.setDbrxm(list.size()>39? list.get(39):null);
//            jyls.setDbrzjlx(list.size()>40? list.get(40):null);
//            jyls.setDbrzjhm(list.size()>41? list.get(41):null);
//            jyls.setDbrlxdhg(list.size()>42? list.get(42):null);
            dataList.add(jyls);
        }
        return dataList;
    }

    public static void main(String[] args) {
        YhzhJylsParser zfbRegParser = new YhzhJylsParser(new Attachment());
        try {
            List<YhzhJylsInfo> info = zfbRegParser.parser("工商银行", "H:\\数据管理系统\\数据导入20180614\\银行模板\\交行交易信息（4条）.xls");
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
