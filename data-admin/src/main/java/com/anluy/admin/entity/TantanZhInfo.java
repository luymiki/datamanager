package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.*;

/**
 * 功能说明：探探账户信息
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class TantanZhInfo extends BaseEntity<String> {
    @JSONField(name = "susp_id")
    private String suspId;

    @JSONField(name = "susp_name")
    private String suspName;

    @JSONField(name = "file_id")
    private String fileId;

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String tags;//标签


    private String yhid;// 用户ID
    private String nc;// 昵称
    private String zcsjqh;// 注册手机区号
    private String sjh;// 手机号

    @JSONField(name = "zcsj", format = "yyyy-MM-dd HH:mm:ss")
    private Date zcsj;// 注册时间
    private String ip;//注册IP
    @JSONField(name = "zhhysj", format = "yyyy-MM-dd HH:mm:ss")
    private Date zhhysj;//	最后活跃时间

    private String zhhyzb;// 最后活跃坐标
    private String zhhycs;// 最后活跃城市
    private String xb;// 性别
    private String csrq;// 出生日期
    private String dqzt;// 当前状态
    private List<String> pics = new ArrayList<>();// 头像

    private List<String> imei = new ArrayList<>();//imei
    private List<String> imsi = new ArrayList<>();//imsi

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

    public Date getZcsj() {
        return zcsj;
    }

    public void setZcsj(Date zcsj) {
        this.zcsj = zcsj;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getZhhysj() {
        return zhhysj;
    }

    public void setZhhysj(Date zhhysj) {
        this.zhhysj = zhhysj;
    }

    public String getZhhyzb() {
        return zhhyzb;
    }

    public void setZhhyzb(String zhhyzb) {
        this.zhhyzb = zhhyzb;
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

    public String getDqzt() {
        return dqzt;
    }

    public void setDqzt(String dqzt) {
        this.dqzt = dqzt;
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

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }
    public void addPic(String pic) {
        this.pics.add(pic);
    }

    public List<String> getImei() {
        return imei;
    }

    public void setImei(List<String> imei) {
        this.imei = imei;
    }
    public void addImei(String imei) {
        this.imei.add(imei);
    }

    public List<String> getImsi() {
        return imsi;
    }

    public void setImsi(List<String> imsi) {
        this.imsi = imsi;
    }
    public void addImsi(String imsi) {
        this.imsi.add(imsi);
    }

}
