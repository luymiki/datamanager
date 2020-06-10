package com.anluy.admin.web.cft.parser;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.CftRegInfo;
import com.anluy.admin.entity.CftTrades;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能说明：财付通流水文件解析
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class CftTradesParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(CftTradesParser.class);
    private final String fileId;

    public CftTradesParser(String fileId) {
        this.fileId = fileId;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<CftTrades> parser(Attachment attachment, File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = null;
        List<String> stringList = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            stringList.add(line);
        }
        return parse(attachment, stringList);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<CftTrades> parser(Attachment attachment, String path) throws Exception {
        return parser(attachment, new File(path));
    }

    private List<CftTrades> parse(Attachment attachment, List<String> txtContent) {
        if (txtContent.size() < 2) {
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        List<CftTrades> dataList = new ArrayList<>();
        for (int i = 2; i < txtContent.size(); i++) {
            String line = txtContent.get(i);
            List<String> list = split(line);
            if (list.size() < 10) {
                continue;
            }
            CftTrades regInfo = new CftTrades();
            regInfo.setFileId(fileId);
            regInfo.setSuspId(attachment.getSuspId());
            regInfo.setSuspName(attachment.getSuspName());
            regInfo.setTags(attachment.getTags());
            regInfo.setZh(list.get(0).replace("[", "").replace("]", ""));
            regInfo.setJydh(list.get(1).replace("[", "").replace("]", ""));
            regInfo.setJdlx(list.get(2));
            regInfo.setJylx(list.get(3));
            if (StringUtils.isNotBlank(list.get(4))) {
                regInfo.setJyje(Double.valueOf(list.get(4)) / 100);
            }
            if (StringUtils.isNotBlank(list.get(5))) {
                regInfo.setJyye(Double.valueOf(list.get(5)) / 100);
            }
            regInfo.setJysj(list.get(6));
            regInfo.setYhlx(list.get(7));
            regInfo.setJysm(list.get(8));
            regInfo.setShmc(list.get(9));
            if (list.size() > 10) {
                regInfo.setFsf(list.get(10));
            }

            if (list.size() > 11 && StringUtils.isNotBlank(list.get(11)) && !"null".equals(list.get(11))) {
                regInfo.setFsje(Double.valueOf(list.get(11)) / 100);
            }
            if (list.size() > 12) {
                regInfo.setJsf(list.get(12));
            }
            if (list.size() > 13) {
                regInfo.setJssj(StringUtils.isBlank(list.get(13)) ? null : list.get(13));
            }
            if (list.size() > 14 && StringUtils.isNotBlank(list.get(14))) {
                regInfo.setJsje(Double.valueOf(list.get(14)) / 100);
            }
            if (StringUtils.isBlank(regInfo.getFsf()) && StringUtils.isBlank(regInfo.getJsf())) {
                regInfo.setDsId("-");
            } else if ("出".equals(regInfo.getJdlx())) {
                if (StringUtils.isBlank(regInfo.getJsf()))
                    regInfo.setDsId("-");
                else
                    regInfo.setDsId(regInfo.getJsf());
            } else {
                if (StringUtils.isBlank(regInfo.getFsf()))
                    regInfo.setDsId("-");
                else
                    regInfo.setDsId(regInfo.getFsf());
            }
            if (regInfo.getJyje() != null) {
                Double mod = regInfo.getJyje() % 100;
                regInfo.setZc100(mod);
            } else {
                regInfo.setZc100(-1.0);
            }
            dataList.add(regInfo);
        }
        return dataList;
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
