package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;
import java.util.List;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/27.
 */
public class QQRegInfo extends BaseEntity<String> {

    private String qq;//qq号
    private String name;//昵称
    private String gj;//国家
    private String sf;//省份
    private String yzbm;//邮编
    private String dz;//地址
    private String dh;//电话
    private String csrq;//生日
    private String xb;//性别
    private String xm;//真实姓名
    private String email;//Email
    private String home;//主页
    private String cs;//城市
    private String bycs;//毕业院校
    private String xz;//星座
    private String zcsj;//注册时间
    private List<String> qqhy;//好友号码
    private List<String> jrqh;//加入的群
    private List<String> cjqh;//创建的群

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JSONField(name = "file_id")
    private String fileId;

    @JSONField(name = "susp_id")
    private String suspId;

    @JSONField(name = "susp_name")
    private String suspName;

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

    public String getGj() {
        return gj;
    }

    public void setGj(String gj) {
        this.gj = gj;
    }

    public String getSf() {
        return sf;
    }

    public void setSf(String sf) {
        this.sf = sf;
    }

    public String getYzbm() {
        return yzbm;
    }

    public void setYzbm(String yzbm) {
        this.yzbm = yzbm;
    }

    public String getDz() {
        return dz;
    }

    public void setDz(String dz) {
        this.dz = dz;
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

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getCs() {
        return cs;
    }

    public void setCs(String cs) {
        this.cs = cs;
    }

    public String getBycs() {
        return bycs;
    }

    public void setBycs(String bycs) {
        this.bycs = bycs;
    }

    public String getXz() {
        return xz;
    }

    public void setXz(String xz) {
        this.xz = xz;
    }

    public List<String> getQqhy() {
        return qqhy;
    }

    public void setQqhy(List<String> qqhy) {
        this.qqhy = qqhy;
    }

    public List<String> getJrqh() {
        return jrqh;
    }

    public void setJrqh(List<String> jrqh) {
        this.jrqh = jrqh;
    }

    public List<String> getCjqh() {
        return cjqh;
    }

    public void setCjqh(List<String> cjqh) {
        this.cjqh = cjqh;
    }

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

    public String getCsrq() {
        return csrq;
    }

    public void setCsrq(String csrq) {
        this.csrq = csrq;
    }

    public String getZcsj() {
        return zcsj;
    }

    public void setZcsj(String zcsj) {
        this.zcsj = zcsj;
    }
}
