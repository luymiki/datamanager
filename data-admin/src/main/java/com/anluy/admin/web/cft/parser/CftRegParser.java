package com.anluy.admin.web.cft.parser;

import com.anluy.admin.entity.CftRegInfo;
import com.anluy.admin.entity.QQRegInfo;
import org.apache.commons.lang3.StringUtils;
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
 * 功能说明：财付通文件解析
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class CftRegParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(CftRegParser.class);
    private final String fileId;

    public CftRegParser(String fileId) {
        this.fileId = fileId;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public CftRegInfo parser(File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
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
    public CftRegInfo parser(String path) throws Exception {
        return parser(new File(path));
    }

    private CftRegInfo parse(List<String> txtContent) {
        if(txtContent.size()<2){
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        CftRegInfo regInfo = new CftRegInfo();
        regInfo.setFileId(fileId);
        List<String> infolist = split( txtContent.get(1));
        if(infolist.size() != 8){
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        regInfo.setZhzt(infolist.get(0));
        regInfo.setZh(infolist.get(1));
        regInfo.setName(infolist.get(2));
        regInfo.setZcsj(infolist.get(3));
        if(StringUtils.isNotBlank(infolist.get(4))){
            regInfo.setSfzh(infolist.get(4).replace("[","").replace("]",""));
        }
        regInfo.setDh(infolist.get(5));
        List<String> kyhList = new ArrayList<>();
        List<String> yhzhList = new ArrayList<>();
        regInfo.setKhxxList(kyhList);
        regInfo.setYhzhList(yhzhList);

        kyhList.add(infolist.get(6));
        if(StringUtils.isNotBlank(infolist.get(7))){
            yhzhList.add(infolist.get(7).replace("[","").replace("]",""));
        }

        for (int i = 2; i < txtContent.size(); i++) {
            String line = txtContent.get(i);
            List<String> list = split( line);
            if(list.size() != 8){
                continue;
            }
            kyhList.add(list.get(6));
            if(StringUtils.isNotBlank(list.get(7))){
                yhzhList.add(list.get(7).replace("[","").replace("]",""));
            }
        }
        return regInfo;
    }

    private List<String> split(String line) {
        String[] infos = line.split("\t");
        List<String> hylist = new ArrayList<>();
        for (String hy : infos) {
            hylist.add(hy.trim());
        }
        return hylist;
    }

}
