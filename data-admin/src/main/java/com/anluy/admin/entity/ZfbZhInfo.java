package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;

/**
 * 功能说明：支付宝账户信息
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class ZfbZhInfo extends BaseEntity<String> {
    @JSONField(name = "file_id")
    private String fileId;

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(name = "susp_id")
    private String suspId;

    @JSONField(name = "susp_name")
    private String suspName;
    private String tags;//标签


    private String jyh;// 交易号
    private String shddh;// 商户订单号
    @JSONField(name = "jycjsj", format = "yyyy-MM-dd HH:mm:ss")
    private Date jycjsj;// 交易创建时间
    @JSONField(name = "fksj", format = "yyyy-MM-dd HH:mm:ss")
    private Date fksj;// 付款时间
    @JSONField(name = "zjxgsj", format = "yyyy-MM-dd HH:mm:ss")
    private Date zjxgsj;// 最近修改时间
    private String jylyd;//交易来源地
    private String jylx;// 类型
    @JSONField(name = "user_id")
    private String userId;// 用户id
    private String name;// 用户名称
    @JSONField(name = "df_user_id")
    private String dfUserId;// 交易对方ID
    @JSONField(name = "df_name")
    private String dfName;// 交易对方信息
    private String xfmc;// 消费名称
    private Double je; // 转账金额（元）
    private String sjbj; // 收/支标记
    private String jyzt; // 交易状态
    private String bz; // 备注
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

    public String getShddh() {
        return shddh;
    }

    public void setShddh(String shddh) {
        this.shddh = shddh;
    }

    public Date getJycjsj() {
        return jycjsj;
    }

    public void setJycjsj(Date jycjsj) {
        this.jycjsj = jycjsj;
    }

    public Date getFksj() {
        return fksj;
    }

    public void setFksj(Date fksj) {
        this.fksj = fksj;
    }

    public Date getZjxgsj() {
        return zjxgsj;
    }

    public void setZjxgsj(Date zjxgsj) {
        this.zjxgsj = zjxgsj;
    }

    public String getJylyd() {
        return jylyd;
    }

    public void setJylyd(String jylyd) {
        this.jylyd = jylyd;
    }

    public String getJylx() {
        return jylx;
    }

    public void setJylx(String jylx) {
        this.jylx = jylx;
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

    public String getDfUserId() {
        return dfUserId;
    }

    public void setDfUserId(String dfUserId) {
        this.dfUserId = dfUserId;
    }

    public String getDfName() {
        return dfName;
    }

    public void setDfName(String dfName) {
        this.dfName = dfName;
    }

    public String getXfmc() {
        return xfmc;
    }

    public void setXfmc(String xfmc) {
        this.xfmc = xfmc;
    }

    public Double getJe() {
        return je;
    }

    public void setJe(Double je) {
        this.je = je;
    }

    public String getSjbj() {
        return sjbj;
    }

    public void setSjbj(String sjbj) {
        this.sjbj = sjbj;
    }

    public String getJyzt() {
        return jyzt;
    }

    public void setJyzt(String jyzt) {
        this.jyzt = jyzt;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
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
