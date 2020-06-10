package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能说明：探探账户好友信息
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class TantanHyxxInfo extends BaseEntity<String> {
    @JSONField(name = "susp_id")
    private String suspId;

    @JSONField(name = "susp_name")
    private String suspName;

    @JSONField(name = "file_id")
    private String fileId;

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String zhid;// 注册信息关联ID

    private String yhid;// 用户ID
    private String nc;// 昵称
    private String zcsjqh;// 注册手机区号
    private String sjh;// 手机号

    private String zhhycs;// 最后活跃城市
    private String xb;// 性别
    private String csrq;// 出生日期
    private String pic;// 头像

    public String getZhid() {
        return zhid;
    }

    public void setZhid(String zhid) {
        this.zhid = zhid;
    }

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

    public String getYhid() {
        return yhid;
    }

    public void setYhid(String yhid) {
        this.yhid = yhid;
    }

    public String getNc() {
        return nc;
    }

    public void setNc(String nc) {
        this.nc = nc;
    }

    public String getZcsjqh() {
        return zcsjqh;
    }

    public void setZcsjqh(String zcsjqh) {
        this.zcsjqh = zcsjqh;
    }

    public String getSjh() {
        return sjh;
    }

    public void setSjh(String sjh) {
        this.sjh = sjh;
    }

    public String getZhhycs() {
        return zhhycs;
    }

    public void setZhhycs(String zhhycs) {
        this.zhhycs = zhhycs;
    }

    public String getXb() {
        return xb;
    }

    public void setXb(String xb) {
        this.xb = xb;
    }

    public String getCsrq() {
        return csrq;
    }

    public void setCsrq(String csrq) {
        this.csrq = csrq;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
