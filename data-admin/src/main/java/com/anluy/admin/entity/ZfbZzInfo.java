package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;

/**
 * 功能说明：支付宝转账信息
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class ZfbZzInfo extends BaseEntity<String> {
    @JSONField(name = "file_id")
    private String fileId;

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String tags;//标签

    private String jyh;// 交易号
    @JSONField(name = "user_id")
    private String userId;// 付款方支付宝账号
    @JSONField(name = "fkf_id")
    private String fkfId;// 付款方支付宝账号
    @JSONField(name = "skf_id")
    private String skfId;// 收款方支付宝账号
    private String skjgmc;// 收款机构信息
    @JSONField(name = "dzsj", format = "yyyy-MM-dd HH:mm:ss")
    private Date dzsj;// 到账时间
    private Double je; // 转账金额（元）
    private String zzcpmc; // 转账产品名称
    private String jyfsd; // 交易发生地
    private String txlsh; // 提现流水号
    private String xcbh;// 对应的协查数据

    private Double jyje;//	交易金额
    @JSONField(name = "jysj", format = "yyyy-MM-dd HH:mm:ss")
    private Date jysj;//	交易时间
    @JSONField(name = "ds_id")
    private String dsId;//	对手id
    private String jdlx;//	借贷标志
    private Double zc100;//	被100整除的余数

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

    public Date getDzsj() {
        return dzsj;
    }

    public void setDzsj(Date dzsj) {
        this.dzsj = dzsj;
    }

    public String getXcbh() {
        return xcbh;
    }

    public void setXcbh(String xcbh) {
        this.xcbh = xcbh;
    }

    public String getJyh() {
        return jyh;
    }

    public void setJyh(String jyh) {
        this.jyh = jyh;
    }

    public String getFkfId() {
        return fkfId;
    }

    public void setFkfId(String fkfId) {
        this.fkfId = fkfId;
    }

    public String getSkfId() {
        return skfId;
    }

    public void setSkfId(String skfId) {
        this.skfId = skfId;
    }

    public String getSkjgmc() {
        return skjgmc;
    }

    public void setSkjgmc(String skjgmc) {
        this.skjgmc = skjgmc;
    }

    public Double getJe() {
        return je;
    }

    public void setJe(Double je) {
        this.je = je;
    }

    public String getZzcpmc() {
        return zzcpmc;
    }

    public void setZzcpmc(String zzcpmc) {
        this.zzcpmc = zzcpmc;
    }

    public String getJyfsd() {
        return jyfsd;
    }

    public void setJyfsd(String jyfsd) {
        this.jyfsd = jyfsd;
    }

    public String getTxlsh() {
        return txlsh;
    }

    public void setTxlsh(String txlsh) {
        this.txlsh = txlsh;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getJyje() {
        return jyje;
    }

    public void setJyje(Double jyje) {
        this.jyje = jyje;
    }

    public Date getJysj() {
        return jysj;
    }

    public void setJysj(Date jysj) {
        this.jysj = jysj;
    }

    public String getDsId() {
        return dsId;
    }

    public void setDsId(String dsId) {
        this.dsId = dsId;
    }

    public String getJdlx() {
        return jdlx;
    }

    public void setJdlx(String jdlx) {
        this.jdlx = jdlx;
    }

    public Double getZc100() {
        return zc100;
    }

    public void setZc100(Double zc100) {
        this.zc100 = zc100;
    }
}
