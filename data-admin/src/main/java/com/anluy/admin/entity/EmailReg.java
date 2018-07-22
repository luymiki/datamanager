package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.*;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/25.
 */
public class EmailReg extends BaseEntity<String> {
    private String email;//email
    private String sjhm;//手机号码
    private String dhhm ;//电话号码
    private String type;//邮件类型：新浪，163
    @JSONField(name = "txt_data")
    private StringBuffer txtData;//原文

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JSONField(name = "file_id")
    private String fileId;

    @JSONField(name = "susp_id")
    private String suspId;

    @JSONField(name = "susp_name")
    private String suspName;

    private String tags;//标签

    private List<EmailIp> iplist = new ArrayList<>();

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSjhm() {
        return sjhm;
    }

    public void setSjhm(String sjhm) {
        this.sjhm = sjhm;
    }

    public String getDhhm() {
        return dhhm;
    }

    public void setDhhm(String dhhm) {
        this.dhhm = dhhm;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public StringBuffer getTxtData() {
        return txtData;
    }

    public void setTxtData(StringBuffer txtData) {
        this.txtData = txtData;
    }

    public List<EmailIp> getIplist() {
        return iplist;
    }

    public void addIplist(EmailIp emailIp) {
        this.iplist.add(emailIp);
    }
}
