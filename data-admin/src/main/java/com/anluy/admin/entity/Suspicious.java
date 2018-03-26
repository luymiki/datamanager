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
    private Integer yhzh;
    private Integer phone;
    private String imei;
    private String glry;
    private String ly;
    private String gzjd;
    @JSONField(name = "create_time",format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(name = "modify_time",format = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;
    @JSONField(name = "is_delete")
    private String isDelete;

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

    public Integer getYhzh() {
        return yhzh;
    }

    public void setYhzh(Integer yhzh) {
        this.yhzh = yhzh;
    }

    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getGlry() {
        return glry;
    }

    public void setGlry(String glry) {
        this.glry = glry;
    }

    public String getLy() {
        return ly;
    }

    public void setLy(String ly) {
        this.ly = ly;
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

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }
}
