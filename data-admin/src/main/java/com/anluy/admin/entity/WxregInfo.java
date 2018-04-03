package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;
import java.util.List;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/4/2.
 */
public class WxregInfo extends BaseEntity<String> {


    @JSONField(name = "file_id")
    private String fileId;

    @JSONField(name = "susp_id")
    private String suspId;

    @JSONField(name = "susp_name")
    private String suspName;

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    private String weixin;//微信号
    private String qq;
    private String name; //昵称
    private String email;
    private String bm;//别名
    private String qm;//签名
    private String dh;//手机号
    private String xb;//性别
    private String sf;//省份
    private String cs;//城市

    private List<Wxlxr> wxlxrList;
    private List<Wxqun> wxqunList;
    private List<Wxloginip> wxloginipList;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBm() {
        return bm;
    }

    public void setBm(String bm) {
        this.bm = bm;
    }

    public String getQm() {
        return qm;
    }

    public void setQm(String qm) {
        this.qm = qm;
    }

    public String getDh() {
        return dh;
    }

    public void setDh(String dh) {
        this.dh = dh;
    }

    public String getXb() {
        return xb;
    }

    public void setXb(String xb) {
        this.xb = xb;
    }

    public String getSf() {
        return sf;
    }

    public void setSf(String sf) {
        this.sf = sf;
    }

    public String getCs() {
        return cs;
    }

    public void setCs(String cs) {
        this.cs = cs;
    }

    public List<Wxlxr> getWxlxrList() {
        return wxlxrList;
    }

    public void setWxlxrList(List<Wxlxr> wxlxrList) {
        this.wxlxrList = wxlxrList;
    }

    public List<Wxqun> getWxqunList() {
        return wxqunList;
    }

    public void setWxqunList(List<Wxqun> wxqunList) {
        this.wxqunList = wxqunList;
    }

    public List<Wxloginip> getWxloginipList() {
        return wxloginipList;
    }

    public void setWxloginipList(List<Wxloginip> wxloginipList) {
        this.wxloginipList = wxloginipList;
    }
}
