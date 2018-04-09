package com.anluy.admin.web.xndw.parser;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.QQRegInfo;
import com.anluy.admin.entity.XndwWskInfo;
import com.anluy.admin.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class XndwWskParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(XndwWskParser.class);
    private final String fileId;

    public XndwWskParser(String fileId) {
        this.fileId = fileId;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<XndwWskInfo> parser(Attachment attachment,File file) throws Exception {
        List<List<String>> list = ExcelUtils.read(file);
        return parse(attachment,list);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<XndwWskInfo> parser(Attachment attachment,String path) throws Exception {
        return parser(attachment,new File(path));
    }

    private List<XndwWskInfo> parse(Attachment attachment,List<List<String>> dataList) throws ParseException {
        List<XndwWskInfo> resultList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int start = -1;
        for (int i = 0; i < dataList.size(); i++) {
            List<String> line = dataList.get(i);
            if (line.size() == 15 && "id".equals(line.get(0)) && "wechatId".equals(line.get(1))) {
                start = i + 1;
                continue;
            }
            if (start > 0 && line.size() == 15) {
                XndwWskInfo regInfo = new XndwWskInfo();
                regInfo.setId(line.get(0).replace(".00",""));
                regInfo.setWechatId(line.get(1));
                regInfo.setMobilephone(line.get(2));
                regInfo.setImei(line.get(3).replace(".00",""));
                regInfo.setQq(line.get(4));
                regInfo.setEmail(line.get(5));
                regInfo.setFacebook(line.get(6));
                regInfo.setIp(line.get(7));
                regInfo.setToken(line.get(8));
                regInfo.setLatitude(line.get(9));
                regInfo.setLongtude(line.get(10));
                regInfo.setVaddr(line.get(11));
                regInfo.setAdword(line.get(12));
                if(StringUtils.isNotBlank(line.get(13))){
                    regInfo.setTime(simpleDateFormat2.parse(line.get(13)));
                }
                regInfo.setAddress(line.get(14));
                regInfo.setTags(attachment.getTags());
                resultList.add(regInfo);
            }
        }
        return resultList;
    }
}
