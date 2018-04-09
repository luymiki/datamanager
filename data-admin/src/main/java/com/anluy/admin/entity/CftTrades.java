package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;
import java.util.List;

/**
 * 功能说明：财付通流水信息
 * <p>
 * Created by hc.zeng on 2018/4/5.
 */
public class CftTrades extends BaseEntity<String> {

    @JSONField(name = "file_id")
    private String fileId;

    @JSONField(name = "susp_id")
    private String suspId;

    @JSONField(name = "susp_name")
    private String suspName;

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String tags;//标签
    @JSONField(name = "cft_id")
    private String cftId;//财付通账号ID
    private String zh;//用户ID
    private String jydh;//交易单号
    private String jdlx; //借贷类型
    private String jylx;//交易类型
    private Double jyje;//交易金额
    private Double jyye;//账户余额
    private String jysj;//交易时间
    private String yhlx;//银行类型
    private String jysm;//交易说明
    private String shmc;//商户名称
    private String fsf;//发送方
    private Double fsje;//发送金额
    private String jsf;//接收方
    private String jssj;//接收时间
    private Double jsje;//接收金额


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

    public String getZh() {
        return zh;
    }

    public void setZh(String zh) {
        this.zh = zh;
    }

    public String getJydh() {
        return jydh;
    }

    public void setJydh(String jydh) {
        this.jydh = jydh;
    }

    public String getJdlx() {
        return jdlx;
    }

    public void setJdlx(String jdlx) {
        this.jdlx = jdlx;
    }

    public String getJylx() {
        return jylx;
    }

    public void setJylx(String jylx) {
        this.jylx = jylx;
    }

    public Double getJyje() {
        return jyje;
    }

    public void setJyje(Double jyje) {
        this.jyje = jyje;
    }

    public Double getJyye() {
        return jyye;
    }

    public void setJyye(Double jyye) {
        this.jyye = jyye;
    }

    public String getJysj() {
        return jysj;
    }

    public void setJysj(String jysj) {
        this.jysj = jysj;
    }

    public String getYhlx() {
        return yhlx;
    }

    public void setYhlx(String yhlx) {
        this.yhlx = yhlx;
    }

    public String getJysm() {
        return jysm;
    }

    public void setJysm(String jysm) {
        this.jysm = jysm;
    }

    public String getShmc() {
        return shmc;
    }

    public void setShmc(String shmc) {
        this.shmc = shmc;
    }

    public String getFsf() {
        return fsf;
    }

    public void setFsf(String fsf) {
        this.fsf = fsf;
    }

    public Double getFsje() {
        return fsje;
    }

    public void setFsje(Double fsje) {
        this.fsje = fsje;
    }

    public String getJsf() {
        return jsf;
    }

    public void setJsf(String jsf) {
        this.jsf = jsf;
    }

    public String getJssj() {
        return jssj;
    }

    public void setJssj(String jssj) {
        this.jssj = jssj;
    }

    public Double getJsje() {
        return jsje;
    }

    public void setJsje(Double jsje) {
        this.jsje = jsje;
    }

    public String getCftId() {
        return cftId;
    }

    public void setCftId(String cftId) {
        this.cftId = cftId;
    }
}
