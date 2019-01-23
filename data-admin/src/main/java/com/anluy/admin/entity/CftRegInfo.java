package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;
import java.util.List;

/**
 * 功能说明：财付通注册信息
 * <p>
 * Created by hc.zeng on 2018/4/5.
 */
public class CftRegInfo  extends BaseEntity<String> {

    @JSONField(name = "file_id")
    private String fileId;

    @JSONField(name = "susp_id")
    private String suspId;

    @JSONField(name = "susp_name")
    private String suspName;

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String tags;//标签

    private String zhzt;//账户状态
    private String zh;//账号
    private String name; //注册姓名
    private String zcsj;//注册时间
    private String sfzh;//注册身份证号
    private String dh;//手机号
    @JSONField(name = "khxx_list")
    private List<String> khxxList;//开户行信息
    @JSONField(name = "yhzh_list")
    private List<String> yhzhList;//银行账号

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

    public String getZhzt() {
        return zhzt;
    }

    public void setZhzt(String zhzt) {
        this.zhzt = zhzt;
    }

    public String getZh() {
        return zh;
    }

    public void setZh(String zh) {
        this.zh = zh;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZcsj() {
        return zcsj;
    }

    public void setZcsj(String zcsj) {
        this.zcsj = zcsj;
    }

    public String getSfzh() {
        return sfzh;
    }

    public void setSfzh(String sfzh) {
        this.sfzh = sfzh;
    }

    public String getDh() {
        return dh;
    }

    public void setDh(String dh) {
        this.dh = dh;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
