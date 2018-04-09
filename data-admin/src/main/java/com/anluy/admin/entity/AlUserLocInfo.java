package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;

/**
 * 功能说明：神行虚拟定位数据
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
public class AlUserLocInfo extends BaseEntity<String> {

    @JSONField(name = "created_at")
    private Date createdAt;
    @JSONField(name = "real_lat_e6")
    private int realLatE6;//真实纬度
    @JSONField(name = "real_lon_e6")
    private int realLonE6;//真实经度
    @JSONField(name = "real_addr")
    private String realAddr;//真实地址
    @JSONField(name = "mock_info")
    private String mockInfo;//虚拟位置信息
    private String imei;//手机串号
    private String imsi;//国际移动用户识别码
    private String model_type;//设备类型
    private String ip;
    @JSONField(name = "phone_num")
    private String phoneNum;//手机号码
    private String email;
    private String contacts;//运营商
    private String sms;
    @JSONField(name = "call_log")
    private String callLog;//
    private String lang;//语言

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getRealLatE6() {
        return realLatE6;
    }

    public void setRealLatE6(int realLatE6) {
        this.realLatE6 = realLatE6;
    }

    public int getRealLonE6() {
        return realLonE6;
    }

    public void setRealLonE6(int realLonE6) {
        this.realLonE6 = realLonE6;
    }

    public String getRealAddr() {
        return realAddr;
    }

    public void setRealAddr(String realAddr) {
        this.realAddr = realAddr;
    }

    public String getMockInfo() {
        return mockInfo;
    }

    public void setMockInfo(String mockInfo) {
        this.mockInfo = mockInfo;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getModel_type() {
        return model_type;
    }

    public void setModel_type(String model_type) {
        this.model_type = model_type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getCallLog() {
        return callLog;
    }

    public void setCallLog(String callLog) {
        this.callLog = callLog;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
