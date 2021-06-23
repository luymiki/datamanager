package com.anluy.admin.web.yhzh2.parser2;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.YhzhKhxxInfo;
import com.anluy.admin.utils.ClassUtils;
import com.anluy.admin.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.function.Function;

/**
 * 功能说明：银行账号开户信息文件解析
 * <p>
 * Created by hc.zeng on 2018/6/25.
 */
public class YhzhKhxxParser2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(YhzhKhxxParser2.class);
    private final Attachment attachment;

    public YhzhKhxxParser2(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<YhzhKhxxInfo> parser(String ssyh, File file, int sheetIndex) throws Exception {
        List<List<String>> list = ExcelUtils.read(file, sheetIndex);
        return parse(ssyh, list);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<YhzhKhxxInfo> parser(String ssyh, String path, int sheetIndex) throws Exception {
        return parser(ssyh, new File(path), sheetIndex);
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

            if ("客户信息".equals(list.get(1))) {
                if(titleList ==null || titleList.isEmpty()){
                    titleList = txtContent.get(i);
                    titleMapping = ClassUtils.loadIndexMapping(YhzhKhxxInfo.class,titleList);
                }
                i++;
                baseList = txtContent.get(i);
                continue;
            } else if ("账户信息".equals(list.get(1))) {
                if(subTitleList ==null || subTitleList.isEmpty()){
                    subTitleList = txtContent.get(i);
                    subTitleMapping = ClassUtils.loadIndexMapping(YhzhKhxxInfo.class,subTitleList);
                }
                i += 1;
                list = txtContent.get(i);
                if (baseList == null) {
                    continue;
                }

            }else if ("关联子账户信息".equals(list.get(1))) {
                i += 1;
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
                khxx.setSuspId(attachment.getSuspId());
                khxx.setSuspName(attachment.getSuspName());
                khxx.setSsyh(ssyh);
                ClassUtils.setDataToObject(khxx,baseList,titleMapping,functionMap);
                ClassUtils.setDataToObject(khxx,list,subTitleMapping,functionMap);
                dataList.add(khxx);
            }
        }
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
        YhzhKhxxParser2 zfbRegParser = new YhzhKhxxParser2(new Attachment());
        try {
            List<YhzhKhxxInfo> info = zfbRegParser.parser("工商银行", "H:\\数据管理系统\\数据导入20180903\\20190130（19）.xls", 0);
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
