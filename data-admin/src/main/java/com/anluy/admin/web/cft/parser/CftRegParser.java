package com.anluy.admin.web.cft.parser;

import com.anluy.admin.entity.CftRegInfo;
import com.anluy.admin.entity.QQRegInfo;
import com.anluy.admin.utils.CSVReader;
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
        CSVReader csvReader = new CSVReader(bufferedReader,'\t');
        List< List<String>> stringList = csvReader.readAll();
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

    private CftRegInfo parse(List< List<String>> txtContent) {
        if(txtContent.size()<2){
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        CftRegInfo regInfo = new CftRegInfo();
        regInfo.setFileId(fileId);
        List<String> infolist = txtContent.get(1);
        if(infolist.size() < 5){
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        regInfo.setZhzt(infolist.size()>0? infolist.get(0):null);
        regInfo.setZh(infolist.size()>1? infolist.get(1):null);
        regInfo.setName(infolist.size()>2? infolist.get(2):null);
        regInfo.setZcsj(infolist.size()>3? infolist.get(3):null);
        String i4 = infolist.size()>4? infolist.get(4):null;
        if(StringUtils.isNotBlank(i4)){
            regInfo.setSfzh(i4.replace("[","").replace("]",""));
        }
        regInfo.setDh(infolist.size()>5? infolist.get(5):null);
        regInfo.setBdzt(infolist.size()>6? infolist.get(6):null);
        List<String> kyhList = new ArrayList<>();
        List<String> yhzhList = new ArrayList<>();
        regInfo.setKhxxList(kyhList);
        regInfo.setYhzhList(yhzhList);

        if(infolist.size()>7){
            kyhList.add(infolist.get(7));
        }
        String i7 = infolist.size()>8? infolist.get(8):null;
        if(StringUtils.isNotBlank(i7)){
            yhzhList.add(i7.replace("[","").replace("]",""));
        }
        boolean xiaoho = false;
        for (int i = 2; i < txtContent.size(); i++) {
            List<String> list = txtContent.get(i);
            if("注销信息".equals(list.get(0))){
                i++;
                xiaoho = true;
                continue;
            }
            if(xiaoho){
                xiaohu(regInfo,list,kyhList,yhzhList);
            }else {
                none(regInfo,list,kyhList,yhzhList);
            }
        }
        return regInfo;
    }

    /**
     * 默认处理
     */
    private void none( CftRegInfo regInfo,List<String> list, List<String> kyhList,List<String> yhzhList){
        if(list.size() <8 ){
            return;
        }
        if(list.size()>7){
            kyhList.add(list.get(7));
        }
        String ii7 = list.size()>8? list.get(8):null;
        if(StringUtils.isNotBlank(ii7)){
            String yhkh = ii7.replace("[","").replace("]","");
            boolean b = true;
            for (int j = 0; j <yhkh.length() ; j++) {
                try{
                    Integer.valueOf(yhkh.charAt(j));
                }catch (Exception e){
                    LOGGER.error(yhkh+"不是银行卡号");
                    b = false;
                }
            }
            if(b){
                yhzhList.add(yhkh);
            }
        }
    }
    /**
     * 注销信息处理
     */
    private void xiaohu(CftRegInfo regInfo,List<String> list, List<String> kyhList,List<String> yhzhList){
        String i2 = list.size()>2? list.get(2):null;
        if(StringUtils.isNotBlank(i2)&& StringUtils.isBlank(regInfo.getName())){
            regInfo.setName(i2);
        }
        String i3 = list.size()>3? list.get(3):null;
        if(StringUtils.isNotBlank(i3)){
            String sfzh = i3.replace("[","").replace("]","");
            if(regInfo.getSfzh()!=null && regInfo.getSfzh().indexOf(sfzh)<0){
                regInfo.setSfzh(regInfo.getSfzh()+","+sfzh);
            }else if(regInfo.getSfzh()==null){
                regInfo.setSfzh(sfzh);
            }
        }
        if(list.size() <7 ){
            return;
        }
        if(list.size()>6){
            kyhList.add(list.get(6));
        }
        String ii7 = list.size()>7? list.get(7):null;
        if(StringUtils.isNotBlank(ii7)){
            String yhkh = ii7.replace("[","").replace("]","");
            boolean b = true;
            for (int j = 0; j <yhkh.length() ; j++) {
                try{
                    Integer.valueOf(yhkh.charAt(j));
                }catch (Exception e){
                    LOGGER.error(yhkh+"不是银行卡号");
                    b = false;
                }
            }
            if(b){
                yhzhList.add(yhkh);
            }
        }
    }

    private List<String> split(String line) {
        String[] infos = line.split("\t");
        List<String> hylist = new ArrayList<>();
        for (String hy : infos) {
            hylist.add(hy.trim());
        }
        return hylist;
    }

    public static void main(String[] args) throws Exception {
        new CftRegParser("").parser("I:\\tp_2693271184_info.txt");
    }
}
