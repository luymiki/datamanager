package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;

/**
 * 功能说明：WSK虚拟定位数据
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
public class XndwWskInfo extends BaseEntity<String> {

    @JSONField(name = "wechat_id")
    private String wechatId;

    private String mobilephone;//移动电话
    private String imei;//手机串号
    private String qq;//qq
    private String email;

    private String facebook;//facebook
    private String ip;
    private String token;


    private String latitude;//纬度

    private String longtude;//经度
    private String vaddr;//虚拟地址

    private String adword;//发送内容

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;//时间
    private String address;//真实地址
    private String tags;//标签


    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtude() {
        return longtude;
    }

    public void setLongtude(String longtude) {
        this.longtude = longtude;
    }

    public String getVaddr() {
        return vaddr;
    }

    public void setVaddr(String vaddr) {
        this.vaddr = vaddr;
    }

    public String getAdword() {
        return adword;
    }

    public void setAdword(String adword) {
        this.adword = adword;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
