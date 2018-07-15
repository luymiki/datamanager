package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;

/**
 * 功能说明：快递运单信息
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class KdydxxInfo extends BaseEntity<String> {
    @JSONField(name = "file_id")
    private String fileId;

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String tags;//标签
    @JSONField(name = "susp_id")
    private String suspId;

    @JSONField(name = "susp_name")
    private String suspName;
    private String flid;//分类id
    private String ydh;//	单号
    private String pm;//	品名
    private Double zl;//	重量
    private String sjr;//	收件人
    private String dh;//	电话
    private String dz;//	地址
    private Double dsk;//	代收款
    private String fhdh;//	发货单号
    private Double fhzl;//	发货重量
    @JSONField(name = "fhrq", format = "yyyy-MM-dd")
    private Date fhrq;//	发货日期
    private String hpfl;//	货品分类
    private String noname;//	无列名
    private String qszt;//	签收状态
    private String tk;//	退款


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

    public String getYdh() {
        return ydh;
    }

    public void setYdh(String ydh) {
        this.ydh = ydh;
    }

    public String getPm() {
        return pm;
    }

    public void setPm(String pm) {
        this.pm = pm;
    }

    public Double getZl() {
        return zl;
    }

    public void setZl(Double zl) {
        this.zl = zl;
    }

    public String getSjr() {
        return sjr;
    }

    public void setSjr(String sjr) {
        this.sjr = sjr;
    }

    public String getDh() {
        return dh;
    }

    public void setDh(String dh) {
        this.dh = dh;
    }

    public String getDz() {
        return dz;
    }

    public void setDz(String dz) {
        this.dz = dz;
    }

    public Double getDsk() {
        return dsk;
    }

    public void setDsk(Double dsk) {
        this.dsk = dsk;
    }

    public String getFhdh() {
        return fhdh;
    }

    public void setFhdh(String fhdh) {
        this.fhdh = fhdh;
    }

    public Double getFhzl() {
        return fhzl;
    }

    public void setFhzl(Double fhzl) {
        this.fhzl = fhzl;
    }

    public Date getFhrq() {
        return fhrq;
    }

    public void setFhrq(Date fhrq) {
        this.fhrq = fhrq;
    }

    public String getHpfl() {
        return hpfl;
    }

    public void setHpfl(String hpfl) {
        this.hpfl = hpfl;
    }

    public String getNoname() {
        return noname;
    }

    public void setNoname(String noname) {
        this.noname = noname;
    }

    public String getQszt() {
        return qszt;
    }

    public void setQszt(String qszt) {
        this.qszt = qszt;
    }

    public String getTk() {
        return tk;
    }

    public void setTk(String tk) {
        this.tk = tk;
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

    public String getFlid() {
        return flid;
    }

    public void setFlid(String flid) {
        this.flid = flid;
    }
}
