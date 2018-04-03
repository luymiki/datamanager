package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/4/2.
 */
public class Wxlxr extends BaseEntity<String> {
    @JSONField(name = "file_id")
    private String fileId;

    @JSONField(name = "susp_id")
    private String suspId;

    @JSONField(name = "susp_name")
    private String suspName;

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(name = "info_id")
    private String infoId;//微信记录id
    private String weixin;//微信号
    private String zh;//帐号
    private String qq;//绑定QQ
    private String dh;//手机号
    private String email;//邮箱
    private String bm;//别名
    private String wbo;//微博
    private String nc;//昵称

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

    public String getZh() {
        return zh;
    }

    public void setZh(String zh) {
        this.zh = zh;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getDh() {
        return dh;
    }

    public void setDh(String dh) {
        this.dh = dh;
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

    public String getWbo() {
        return wbo;
    }

    public void setWbo(String wbo) {
        this.wbo = wbo;
    }

    public String getNc() {
        return nc;
    }

    public void setNc(String nc) {
        this.nc = nc;
    }

    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }
}
