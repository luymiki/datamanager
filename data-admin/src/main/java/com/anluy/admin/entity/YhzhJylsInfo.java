package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;

/**
 * 功能说明：银行账单交易流水信息
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class YhzhJylsInfo extends BaseEntity<String> {

    @JSONField(name = "file_id")
    private String fileId;

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(name = "susp_id")
    private String suspId;

    @JSONField(name = "susp_name")
    private String suspName;
    private String tags;//标签
    private String ssyh;//	所属银行

    @Field("序号")
    private String xh;//	序号
    @Field("查询反馈结果")
    private String cxfkjg;//	查询反馈结果
    @Field("查询反馈结果原因")
    private String cxfkjgyy;//	查询反馈结果原因
    @Field("查询卡号")
    private String kh;//	查询卡号
    @Field("查询账号")
    private String zh;//	查询账号
    @Field("交易类型")
    private String jylx;//	交易类型
    @Field("借贷标志")
    private String jdlx;//	借贷标志
    @Field("币种")
    private String jybz;//	币种
    @Field("交易金额")
    private Double jyje;//	交易金额
    @Field("交易余额")
    private Double jyye;//	交易余额
    @Field("交易时间")
    @JSONField(name = "jysj", format = "yyyy-MM-dd HH:mm:ss")
    private Date jysj;//	交易时间
    @Field("交易流水号")
    private String jylsh;//	交易流水号
    @Field("交易对方名称")
    private String dfmc;//	交易对方名称
    @Field("交易对方账号")
    private String dfzh;//	交易对方账号
    @Field("交易对方卡号")
    private String dfkh;//	交易对方卡号
    @Field("交易对方证件号码")
    private String dfzjhm;//	交易对方证件号码
    @Field("交易对手余额")
    private String dfye;//	交易对手余额
    @Field("交易对方账号开户行")
    private String dfzhkhh;//	交易对方账号开户行
    @Field("交易摘要")
    private String jyzy;//	交易摘要
    @Field("交易网点名称")
    private String jywdmc;//	交易网点名称
    @Field("交易网点代码")
    private String jywddm;//	交易网点代码
    @Field("日志号")
    private String rzh;//	日志号
    @Field("传票号")
    private String cph;//	传票号
    @Field("凭证种类")
    private String pzzl;//	凭证种类
    @Field("凭证号")
    private String pzh;//	凭证号
    @Field("现金标志")
    private String xjbz;//	现金标志
    @Field("终端号")
    private String zdh;//	终端号
    @Field("交易是否成功")
    private String jysfcg;//	交易是否成功
    @Field("交易发生地")
    private String jyfsd;//	交易发生地
    @Field("商户名称")
    private String shmc;//	商户名称
    @Field("商户号")
    private String shh;//	商户号
    @Field("IP地址")
    private String ip;//	IP地址
    @Field("MAC地址")
    private String mac;//	MAC地址
    @Field("交易柜员号")
    private String jygyh;//	交易柜员号
    @Field("备注")
    private String bz;//	备注
    @Field("交易手续费币种")
    private String jyssfbz;//	交易手续费币种
    @Field("交易手续费金额")
    private Double jyssfje;//	交易手续费金额
    @Field("交易手续费摘要")
    private String jyssfzy;//	交易手续费摘要
    @Field("登录用户名")
    private String dlyhm;//	登录用户名
    @Field("代办人姓名")
    private String dbrxm;//	代办人姓名
    @Field("代办人证件类型")
    private String dbrzjlx;//	代办人证件类型
    @Field("代办人证件号码")
    private String dbrzjhm;//	代办人证件号码
    @Field("代办人联系电话")
    private String dbrlxdhg;//	代办人联系电话

    private Double zc100;//	被100整除的余数

    @JSONField(name = "ds_id")
    private String dsId;//	对手id

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

    public String getSsyh() {
        return ssyh;
    }

    public void setSsyh(String ssyh) {
        this.ssyh = ssyh;
    }

    public String getXh() {
        return xh;
    }

    public void setXh(String xh) {
        this.xh = xh;
    }

    public String getCxfkjg() {
        return cxfkjg;
    }

    public void setCxfkjg(String cxfkjg) {
        this.cxfkjg = cxfkjg;
    }

    public String getCxfkjgyy() {
        return cxfkjgyy;
    }

    public void setCxfkjgyy(String cxfkjgyy) {
        this.cxfkjgyy = cxfkjgyy;
    }

    public String getKh() {
        return kh;
    }

    public void setKh(String kh) {
        this.kh = kh;
    }

    public String getZh() {
        return zh;
    }

    public void setZh(String zh) {
        this.zh = zh;
    }

    public String getJylx() {
        return jylx;
    }

    public void setJylx(String jylx) {
        this.jylx = jylx;
    }

    public String getJdlx() {
        return jdlx;
    }

    public void setJdlx(String jdlx) {
        this.jdlx = jdlx;
    }

    public String getJybz() {
        return jybz;
    }

    public void setJybz(String jybz) {
        this.jybz = jybz;
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

    public Date getJysj() {
        return jysj;
    }

    public void setJysj(Date jysj) {
        this.jysj = jysj;
    }

    public String getJylsh() {
        return jylsh;
    }

    public void setJylsh(String jylsh) {
        this.jylsh = jylsh;
    }

    public String getDfmc() {
        return dfmc;
    }

    public void setDfmc(String dfmc) {
        this.dfmc = dfmc;
    }

    public String getDfzh() {
        return dfzh;
    }

    public void setDfzh(String dfzh) {
        this.dfzh = dfzh;
    }

    public String getDfkh() {
        return dfkh;
    }

    public void setDfkh(String dfkh) {
        this.dfkh = dfkh;
    }

    public String getDfzjhm() {
        return dfzjhm;
    }

    public void setDfzjhm(String dfzjhm) {
        this.dfzjhm = dfzjhm;
    }

    public String getDfye() {
        return dfye;
    }

    public void setDfye(String dfye) {
        this.dfye = dfye;
    }

    public String getDfzhkhh() {
        return dfzhkhh;
    }

    public void setDfzhkhh(String dfzhkhh) {
        this.dfzhkhh = dfzhkhh;
    }

    public String getJyzy() {
        return jyzy;
    }

    public void setJyzy(String jyzy) {
        this.jyzy = jyzy;
    }

    public String getJywdmc() {
        return jywdmc;
    }

    public void setJywdmc(String jywdmc) {
        this.jywdmc = jywdmc;
    }

    public String getJywddm() {
        return jywddm;
    }

    public void setJywddm(String jywddm) {
        this.jywddm = jywddm;
    }

    public String getRzh() {
        return rzh;
    }

    public void setRzh(String rzh) {
        this.rzh = rzh;
    }

    public String getCph() {
        return cph;
    }

    public void setCph(String cph) {
        this.cph = cph;
    }

    public String getPzzl() {
        return pzzl;
    }

    public void setPzzl(String pzzl) {
        this.pzzl = pzzl;
    }

    public String getPzh() {
        return pzh;
    }

    public void setPzh(String pzh) {
        this.pzh = pzh;
    }

    public String getXjbz() {
        return xjbz;
    }

    public void setXjbz(String xjbz) {
        this.xjbz = xjbz;
    }

    public String getZdh() {
        return zdh;
    }

    public void setZdh(String zdh) {
        this.zdh = zdh;
    }

    public String getJysfcg() {
        return jysfcg;
    }

    public void setJysfcg(String jysfcg) {
        this.jysfcg = jysfcg;
    }

    public String getJyfsd() {
        return jyfsd;
    }

    public void setJyfsd(String jyfsd) {
        this.jyfsd = jyfsd;
    }

    public String getShmc() {
        return shmc;
    }

    public void setShmc(String shmc) {
        this.shmc = shmc;
    }

    public String getShh() {
        return shh;
    }

    public void setShh(String shh) {
        this.shh = shh;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getJygyh() {
        return jygyh;
    }

    public void setJygyh(String jygyh) {
        this.jygyh = jygyh;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getJyssfbz() {
        return jyssfbz;
    }

    public void setJyssfbz(String jyssfbz) {
        this.jyssfbz = jyssfbz;
    }

    public Double getJyssfje() {
        return jyssfje;
    }

    public void setJyssfje(Double jyssfje) {
        this.jyssfje = jyssfje;
    }

    public String getJyssfzy() {
        return jyssfzy;
    }

    public void setJyssfzy(String jyssfzy) {
        this.jyssfzy = jyssfzy;
    }

    public String getDlyhm() {
        return dlyhm;
    }

    public void setDlyhm(String dlyhm) {
        this.dlyhm = dlyhm;
    }

    public String getDbrxm() {
        return dbrxm;
    }

    public void setDbrxm(String dbrxm) {
        this.dbrxm = dbrxm;
    }

    public String getDbrzjlx() {
        return dbrzjlx;
    }

    public void setDbrzjlx(String dbrzjlx) {
        this.dbrzjlx = dbrzjlx;
    }

    public String getDbrzjhm() {
        return dbrzjhm;
    }

    public void setDbrzjhm(String dbrzjhm) {
        this.dbrzjhm = dbrzjhm;
    }

    public String getDbrlxdhg() {
        return dbrlxdhg;
    }

    public void setDbrlxdhg(String dbrlxdhg) {
        this.dbrlxdhg = dbrlxdhg;
    }

    public Double getZc100() {
        return zc100;
    }

    public void setZc100(Double zc100) {
        this.zc100 = zc100;
    }

    public String getDsId() {
        return dsId;
    }

    public void setDsId(String dsId) {
        this.dsId = dsId;
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
