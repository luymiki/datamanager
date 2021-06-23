package com.anluy.admin.web.yhzh3.parser3;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.string.StringStringConverter;
import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.admin.entity.Field;
import com.anluy.admin.entity.YhzhKhxxInfo;
import com.anluy.commons.BaseEntity;

import java.util.Date;

/**
 * 功能说明：银行账单开户信息
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class YhzhKhxxInfo3 extends YhzhKhxxInfo {

    @ExcelProperty(value = "序号")
    private String xh;//	序号

    @ExcelProperty(value = "身份证号")
    private String zzhm;//	证照号码

    @ExcelProperty(value = "户名")
    private String name;//	客户名称

    @ExcelProperty(value = "联系电话")
    private String lxdh;//	联系电话

    @ExcelProperty(value = "账号")
    private String zh;//	账号

    @ExcelProperty(value = "开户日期",converter = DateConverter.class)
    @JSONField(name = "khrq", format = "yyyy-MM-dd")
    private Date khrq;//	开户日期

    @ExcelProperty(value = "开户网点名称")
    private String khwd;//	开户网点

    @ExcelProperty(value = "身份证登记住址")
    private String zzdz;//	住宅地址

    @ExcelProperty(value = "开户申请书上的留存地址")
    private String dwdz;//	单位地址

    @ExcelProperty(value = "代办人姓名")
    private String dbrxm;//	代办人姓名

    @ExcelProperty(value = "代办人证件号码")
    private String dbrzjhm;//	代办人证件号码

    @ExcelProperty(value = "账户状态",converter = StringStringConverter.class)
    private String zhztt;

    @Override
    public String getXh() {
        return xh;
    }

    @Override
    public void setXh(String xh) {
        this.xh = xh;
    }

    @Override
    public String getZzhm() {
        return zzhm;
    }

    @Override
    public void setZzhm(String zzhm) {
        this.zzhm = zzhm;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLxdh() {
        return lxdh;
    }

    @Override
    public void setLxdh(String lxdh) {
        this.lxdh = lxdh;
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
    public Date getKhrq() {
        return khrq;
    }

    @Override
    public void setKhrq(Date khrq) {
        this.khrq = khrq;
    }

    @Override
    public String getKhwd() {
        return khwd;
    }

    @Override
    public void setKhwd(String khwd) {
        this.khwd = khwd;
    }

    @Override
    public String getZzdz() {
        return zzdz;
    }

    @Override
    public void setZzdz(String zzdz) {
        this.zzdz = zzdz;
    }

    @Override
    public String getDwdz() {
        return dwdz;
    }

    @Override
    public void setDwdz(String dwdz) {
        this.dwdz = dwdz;
    }

    @Override
    public String getDbrxm() {
        return dbrxm;
    }

    @Override
    public void setDbrxm(String dbrxm) {
        this.dbrxm = dbrxm;
    }

    @Override
    public String getDbrzjhm() {
        return dbrzjhm;
    }

    @Override
    public void setDbrzjhm(String dbrzjhm) {
        this.dbrzjhm = dbrzjhm;
    }

    public String getZhztt() {
        return zhztt;
    }

    public void setZhztt(String zhztt) {
        this.zhztt = zhztt;
    }
}
