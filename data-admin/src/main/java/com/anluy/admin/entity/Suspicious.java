package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
public class Suspicious extends BaseEntity<String> {

    private String name;
    @JSONField(name = "name_na")
    private String nameNa;
    private String gmsfzh;
    private String qq;
    private String weixin;
    private String cft;
    private String zfb;
    private String yhzh;
    private String phone;
    private String imei;
    private String imsi;
    private String email;
    private String type;//类型 1：可疑人员 2：关系人

    @JSONField(name = "susp_id")
    private String suspId;//可疑人员id

    @JSONField(name = "susp_name")
    private String suspName;//可疑人员姓名
    private String qkjj;//情况简介
    private String other; //其他特征
    @JSONField(name = "kyr_id")
    private String kyrId; //可疑人

    private String gzjd;
    @JSONField(name = "create_time",format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(name = "modify_time",format = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.nameNa = name;
    }

    public String getNameNa() {
        nameNa = name;
        return nameNa;
    }

    public void setNameNa(String nameNa) {
        this.nameNa = nameNa;
    }

    public String getGmsfzh() {
        return gmsfzh;
    }

    public void setGmsfzh(String gmsfzh) {
        this.gmsfzh = gmsfzh;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getCft() {
        return cft;
    }

    public void setCft(String cft) {
        this.cft = cft;
    }

    public String getZfb() {
        return zfb;
    }

    public void setZfb(String zfb) {
        this.zfb = zfb;
    }

    public String getYhzh() {
        return yhzh;
    }

    public void setYhzh(String yhzh) {
        this.yhzh = yhzh;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getGzjd() {
        return gzjd;
    }

    public void setGzjd(String gzjd) {
        this.gzjd = gzjd;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSuspId() {
        return suspId;
    }

    public void setSuspId(String suspId) {
        this.suspId = suspId;
    }

    public String getSuspName() {
        return suspName;
    }

    public void setSuspName(String suspName) {
        this.suspName = suspName;
    }

    public String getQkjj() {
        return qkjj;
    }

    public void setQkjj(String qkjj) {
        this.qkjj = qkjj;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getKyrId() {
        return kyrId;
    }

    public void setKyrId(String kyrId) {
        this.kyrId = kyrId;
    }
}
