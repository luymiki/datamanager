package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;
import java.util.List;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class ZfbRegInfo extends BaseEntity<String> {
    @JSONField(name = "file_id")
    private String fileId;
    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String tags;//标签
    @JSONField(name = "susp_id")
    private String suspId;

    @JSONField(name = "susp_name")
    private String suspName;
    @JSONField(name = "user_id")
    private String userId;//用户Id
    private String email;//登陆邮箱
    private String dlsj; //登陆手机
    private String name;//账户名称
    private String zjlx;//证件类型
    private String sfzh;//注册身份证号
    private double ye;//可用余额
    private String bdsj;//绑定手机
    @JSONField(name = "khxx_list")
    private List<String> khxxList;//开户行信息
    @JSONField(name = "yhzh_list")
    private List<String> yhzhList;//银行账号
    private String xcbh;//对应的协查数据

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDlsj() {
        return dlsj;
    }

    public void setDlsj(String dlsj) {
        this.dlsj = dlsj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZjlx() {
        return zjlx;
    }

    public void setZjlx(String zjlx) {
        this.zjlx = zjlx;
    }

    public String getSfzh() {
        return sfzh;
    }

    public void setSfzh(String sfzh) {
        this.sfzh = sfzh;
    }

    public double getYe() {
        return ye;
    }

    public void setYe(double ye) {
        this.ye = ye;
    }

    public String getBdsj() {
        return bdsj;
    }

    public void setBdsj(String bdsj) {
        this.bdsj = bdsj;
    }

    public List<String> getKhxxList() {
        return khxxList;
    }

    public void setKhxxList(List<String> khxxList) {
        this.khxxList = khxxList;
    }

    public List<String> getYhzhList() {
        return yhzhList;
    }

    public void setYhzhList(List<String> yhzhList) {
        this.yhzhList = yhzhList;
    }

    public String getXcbh() {
        return xcbh;
    }

    public void setXcbh(String xcbh) {
        this.xcbh = xcbh;
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
}
