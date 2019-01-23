package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;

/**
 * 功能说明：支付宝交易信息
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class ZfbJyjlInfo extends BaseEntity<String> {
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
    private String userId;// 用户id
    private String name;// 用户名称

    private String jyh;// 交易号
    private String wbjyh;// 外部交易号
    private String jyzt;// 交易状态
    @JSONField(name = "hzhb_id")
    private String hzhbid;// 合作伙伴ID
    @JSONField(name = "mj_id")
    private String mjId;// 买家用户id
    private String mjxx;// 买家信息
    @JSONField(name = "maijia_id")
    private String maijiaid;// 卖家用户id
    private String maijiaxx;// 卖家信息
    private Double je; // 交易金额（元）
    @JSONField(name = "sksj", format = "yyyy-MM-dd HH:mm:ss")
    private Date sksj; // 收款时间
    @JSONField(name = "zhxgsj", format = "yyyy-MM-dd HH:mm:ss")
    private Date zhxgsj; // 最后修改时间
    @JSONField(name = "cjsj", format = "yyyy-MM-dd HH:mm:ss")
    private Date cjsj; // 创建时间
    private String jylx; // 交易类型
    private String lyd; // 来源地
    private String spmc; // 商品名称
    private String shrdz; // 收货人地址
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

    public String getWbjyh() {
        return wbjyh;
    }

    public void setWbjyh(String wbjyh) {
        this.wbjyh = wbjyh;
    }

    public String getJyzt() {
        return jyzt;
    }

    public void setJyzt(String jyzt) {
        this.jyzt = jyzt;
    }

    public String getHzhbid() {
        return hzhbid;
    }

    public void setHzhbid(String hzhbid) {
        this.hzhbid = hzhbid;
    }

    public String getMjId() {
        return mjId;
    }

    public void setMjId(String mjId) {
        this.mjId = mjId;
    }

    public String getMjxx() {
        return mjxx;
    }

    public void setMjxx(String mjxx) {
        this.mjxx = mjxx;
    }

    public String getMaijiaid() {
        return maijiaid;
    }

    public void setMaijiaid(String maijiaid) {
        this.maijiaid = maijiaid;
    }

    public String getMaijiaxx() {
        return maijiaxx;
    }

    public void setMaijiaxx(String maijiaxx) {
        this.maijiaxx = maijiaxx;
    }

    public Double getJe() {
        return je;
    }

    public void setJe(Double je) {
        this.je = je;
    }

    public Date getSksj() {
        return sksj;
    }

    public void setSksj(Date sksj) {
        this.sksj = sksj;
    }

    public Date getZhxgsj() {
        return zhxgsj;
    }

    public void setZhxgsj(Date zhxgsj) {
        this.zhxgsj = zhxgsj;
    }

    public Date getCjsj() {
        return cjsj;
    }

    public void setCjsj(Date cjsj) {
        this.cjsj = cjsj;
    }

    public String getJylx() {
        return jylx;
    }

    public void setJylx(String jylx) {
        this.jylx = jylx;
    }

    public String getLyd() {
        return lyd;
    }

    public void setLyd(String lyd) {
        this.lyd = lyd;
    }

    public String getSpmc() {
        return spmc;
    }

    public void setSpmc(String spmc) {
        this.spmc = spmc;
    }

    public String getShrdz() {
        return shrdz;
    }

    public void setShrdz(String shrdz) {
        this.shrdz = shrdz;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
