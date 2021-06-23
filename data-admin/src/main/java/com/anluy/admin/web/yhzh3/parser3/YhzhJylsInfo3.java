package com.anluy.admin.web.yhzh3.parser3;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.admin.entity.Field;
import com.anluy.admin.entity.YhzhJylsInfo;

import java.util.Date;

/**
 * 功能说明：银行账单交易流水信息
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class YhzhJylsInfo3 extends YhzhJylsInfo {

    @ExcelProperty(value = "序号")
    private String xh;

    @ExcelProperty(value = "账号")
    private String zh;

    @ExcelProperty(value = "借贷标志（借方：1，贷方：2）",converter = JdbzConverter.class)
    private String jdlx;

    @ExcelProperty(value = "金额（元）",converter = DoubleConverter.class)
    private Double jyje;//	交易金额
    @ExcelProperty(value = "交易金额",converter = DoubleConverter.class)
    private Double jyje2;//	交易金额

    @ExcelProperty(value = "账户余额",converter = DoubleConverter.class)
    private Double jyye;//	交易余额

    @ExcelProperty(value = "交易日期（YYYYMMDD）")
    private String jyrq;//	交易日期
    @ExcelProperty(value = "交易日期（YYYYMM11）")
    private String jyrq2;//	交易日期

    @ExcelProperty(value = "交易时间（HHMMSS）")
    private String jysjStr;//	交易时间
    @ExcelProperty(value = "交易时间")
    private String jysjStr2;//	交易时间

    @ExcelProperty(value = "对方户名")
    private String dfmc;//	交易对方名称

    @ExcelProperty(value = "对方账号")
    private String dfzh;//	交易对方账号

    @ExcelProperty(value = "对方行名")
    private String dfzhkhh;//	交易对方账号开户行

    @ExcelProperty(value = "摘要")
    private String jyzy;//	交易摘要

    @ExcelProperty(value = "IP地址")
    private String ip;//	IP地址

    public String getJyrq() {
        if(jyrq ==null){
            jyrq = jyrq2;
        }
        return jyrq;
    }

    public void setJyrq(String jyrq) {
        this.jyrq = jyrq;
    }

    public String getJysjStr() {
        if(jysjStr ==null){
            jysjStr = jysjStr2;
        }
        return jysjStr;
    }

    public void setJysjStr(String jysjStr) {
        this.jysjStr = jysjStr;
    }

    @Override
    public String getXh() {
        return xh;
    }

    @Override
    public void setXh(String xh) {
        this.xh = xh;
    }

    @Override
    public String getZh() {
        return zh;
    }

    @Override
    public void setZh(String zh) {
        this.zh = zh;
    }

    @Override
    public String getJdlx() {
        return jdlx;
    }

    @Override
    public void setJdlx(String jdlx) {
        this.jdlx = jdlx;
    }

    @Override
    public Double getJyje() {
        if(jyje ==null){
            jyje = jyje2;
        }
        return jyje;
    }

    @Override
    public void setJyje(Double jyje) {
        this.jyje = jyje;
    }

    @Override
    public Double getJyye() {
        return jyye;
    }

    @Override
    public void setJyye(Double jyye) {
        this.jyye = jyye;
    }

    @Override
    public String getDfmc() {
        return dfmc;
    }

    @Override
    public void setDfmc(String dfmc) {
        this.dfmc = dfmc;
    }

    @Override
    public String getDfzh() {
        return dfzh;
    }

    @Override
    public void setDfzh(String dfzh) {
        this.dfzh = dfzh;
    }

    @Override
    public String getDfzhkhh() {
        return dfzhkhh;
    }

    @Override
    public void setDfzhkhh(String dfzhkhh) {
        this.dfzhkhh = dfzhkhh;
    }

    @Override
    public String getJyzy() {
        return jyzy;
    }

    @Override
    public void setJyzy(String jyzy) {
        this.jyzy = jyzy;
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public void setIp(String ip) {
        this.ip = ip;
    }

    public Double getJyje2() {
        return jyje2;
    }

    public void setJyje2(Double jyje2) {
        this.jyje2 = jyje2;
    }

    public String getJyrq2() {
        return jyrq2;
    }

    public void setJyrq2(String jyrq2) {
        this.jyrq2 = jyrq2;
    }

    public String getJysjStr2() {
        return jysjStr2;
    }

    public void setJysjStr2(String jysjStr2) {
        this.jysjStr2 = jysjStr2;
    }
}
