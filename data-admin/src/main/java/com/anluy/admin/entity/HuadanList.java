package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;
import java.util.List;

/**
 * 功能说明：话单详情信息
 * <p>
 * Created by hc.zeng on 2018/4/5.
 */
public class HuadanList extends BaseEntity<String> {

    @JSONField(name = "file_id")
    private String fileId;

    @JSONField(name = "susp_id")
    private String suspId;

    @JSONField(name = "susp_name")
    private String suspName;

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String tags;//标签

    @JSONField(name = "hd_id")
    private String hdId;//话单id

    private String yys;//运营商
    private String thlx;//通话类型
    private String thdd; //通话地点
    private String zjhm;//主机号码
    private String ddhm;//对端号码
    private String ddhmgsd;//对端号码归属地
    private String kssj;//开始时间
    private String jssj;//结束时间
    private Long thsc;//通话时长
    private String hjlx;//呼叫类型
    private String xqh;//小区号


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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getHdId() {
        return hdId;
    }

    public void setHdId(String hdId) {
        this.hdId = hdId;
    }

    public String getYys() {
        return yys;
    }

    public void setYys(String yys) {
        this.yys = yys;
    }

    public String getThlx() {
        return thlx;
    }

    public void setThlx(String thlx) {
        this.thlx = thlx;
    }

    public String getThdd() {
        return thdd;
    }

    public void setThdd(String thdd) {
        this.thdd = thdd;
    }

    public String getZjhm() {
        return zjhm;
    }

    public void setZjhm(String zjhm) {
        this.zjhm = zjhm;
    }

    public String getDdhm() {
        return ddhm;
    }

    public void setDdhm(String ddhm) {
        this.ddhm = ddhm;
    }

    public String getKssj() {
        return kssj;
    }

    public void setKssj(String kssj) {
        this.kssj = kssj;
    }

    public String getJssj() {
        return jssj;
    }

    public void setJssj(String jssj) {
        this.jssj = jssj;
    }

    public Long getThsc() {
        return thsc;
    }

    public void setThsc(Long thsc) {
        this.thsc = thsc;
    }

    public String getHjlx() {
        return hjlx;
    }

    public void setHjlx(String hjlx) {
        this.hjlx = hjlx;
    }

    public String getXqh() {
        return xqh;
    }

    public void setXqh(String xqh) {
        this.xqh = xqh;
    }

    public String getDdhmgsd() {
        return ddhmgsd;
    }

    public void setDdhmgsd(String ddhmgsd) {
        this.ddhmgsd = ddhmgsd;
    }
}
