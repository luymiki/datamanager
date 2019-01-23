package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;

/**
 * 功能说明：银行账单开户信息
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class YhzhKhxxInfo extends BaseEntity<String> {
    @JSONField(name = "file_id")
    private String fileId;

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String tags;//标签
    @JSONField(name = "susp_id")
    private String suspId;

    @JSONField(name = "susp_name")
    private String suspName;
    private String ssyh;//	所属银行
    @Field("序号")
    private String xh;//	序号
    @Field("查询反馈结果")
    private String cxfkjg;//	查询反馈结果
    @Field("证照类型")
    private String zzlx;//	证照类型
    @Field("证照号码")
    private String zzhm;//	证照号码
    @Field("客户名称")
    private String name;//	客户名称
    @Field("联系电话")
    private String lxdh;//	联系电话
    @Field("账卡号")
    private String kzhinfo;//	卡账号信息
    @Field("卡号")
    private String kh;//	卡号
    @Field("账号")
    private String zh;//	账号
    @Field("开户日期")
    @JSONField(name = "khrq", format = "yyyy-MM-dd")
    private Date khrq;//	开户日期
    @Field("销户日期")
    @JSONField(name = "xhrq", format = "yyyy-MM-dd")
    private Date xhrq;//	销户日期
    @Field("开户网点")
    private String khwd;//	开户网点
    @Field("联系手机")
    private String lxsj;//	联系手机
    @Field("代办人姓名")
    private String dbrxm;//	代办人姓名
    @Field("代办人证件类型")
    private String dbrzjlx;//	代办人证件类型
    @Field("代办人证件号码")
    private String dbrzjhm;//	代办人证件号码
    @Field("住宅地址")
    private String zzdz;//	住宅地址
    @Field("住宅电话")
    private String zzdh;//	住宅电话
    @Field("工作单位")
    private String gzdw;//	工作单位
    @Field("单位地址")
    private String dwdz;//	单位地址
    @Field("单位电话")
    private String dwdh;//	单位电话
    @Field("邮箱地址")
    private String email;//	邮箱地址
    @Field("法人代表")
    private String frdb;//	法人代表
    @Field("法人代表证件类型")
    private String frdbzjlx;//	法人代表证件类型
    @Field("法人代表证件号码")
    private String frdbzjhm;//	法人代表证件号码
    @Field("客户工商执照号码")
    private String khgszzhm;//	客户工商执照号码
    @Field("国税纳税号")
    private String gsnsh;//	国税纳税号
    @Field("地税纳税号")
    private String dsnsh;//	地税纳税号
    @Field("代办人联系电话")
    private String dbrlxdh;//	代办人联系电话
    @Field("网银账户名")
    private String wyzhm;//	网银账户名
    @Field("网银办理日期")
    @JSONField(name = "wyblrq", format = "yyyy-MM-dd HH:mm:ss")
    private Date wyblrq;//	网银办理日期
    @Field("网银开户网点")
    private String wykhwd;//	网银开户网点
    @Field("网银开户网点代码")
    private String wykhwddm;//	网银开户网点代码
    @Field("网银开户网点所在地")
    private String wykhwdszd;//	网银开户网点所在地
    @Field("手机银行账户名")
    private String sjyhzhm;//	手机银行账户名
    @Field("手机银行办理日期")
    @JSONField(name = "sjyhblrq", format = "yyyy-MM-dd HH:mm:ss")
    private Date sjyhblrq;//	手机银行办理日期
    @Field("手机银行开户网点")
    private String sjyhkhwd;//	手机银行开户网点
    @Field("手机银行开户网点代码")
    private String sjyhkhwddm;//	手机银行开户网点代码
    @Field("手机银行开户网点所在地")
    private String sjyhkhwdszd;//	手机银行开户网点所在地
    private Integer ljjybs;//	交易流水记录数

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

    public String getZzlx() {
        return zzlx;
    }

    public void setZzlx(String zzlx) {
        this.zzlx = zzlx;
    }

    public String getZzhm() {
        return zzhm;
    }

    public void setZzhm(String zzhm) {
        this.zzhm = zzhm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLxdh() {
        return lxdh;
    }

    public void setLxdh(String lxdh) {
        this.lxdh = lxdh;
    }

    public String getKzhinfo() {
        return kzhinfo;
    }

    public void setKzhinfo(String kzhinfo) {
        this.kzhinfo = kzhinfo;
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

    public Date getKhrq() {
        return khrq;
    }

    public void setKhrq(Date khrq) {
        this.khrq = khrq;
    }

    public Date getXhrq() {
        return xhrq;
    }

    public void setXhrq(Date xhrq) {
        this.xhrq = xhrq;
    }

    public String getKhwd() {
        return khwd;
    }

    public void setKhwd(String khwd) {
        this.khwd = khwd;
    }

    public String getLxsj() {
        return lxsj;
    }

    public void setLxsj(String lxsj) {
        this.lxsj = lxsj;
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

    public String getZzdz() {
        return zzdz;
    }

    public void setZzdz(String zzdz) {
        this.zzdz = zzdz;
    }

    public String getZzdh() {
        return zzdh;
    }

    public void setZzdh(String zzdh) {
        this.zzdh = zzdh;
    }

    public String getGzdw() {
        return gzdw;
    }

    public void setGzdw(String gzdw) {
        this.gzdw = gzdw;
    }

    public String getDwdz() {
        return dwdz;
    }

    public void setDwdz(String dwdz) {
        this.dwdz = dwdz;
    }

    public String getDwdh() {
        return dwdh;
    }

    public void setDwdh(String dwdh) {
        this.dwdh = dwdh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFrdb() {
        return frdb;
    }

    public void setFrdb(String frdb) {
        this.frdb = frdb;
    }

    public String getFrdbzjlx() {
        return frdbzjlx;
    }

    public void setFrdbzjlx(String frdbzjlx) {
        this.frdbzjlx = frdbzjlx;
    }

    public String getFrdbzjhm() {
        return frdbzjhm;
    }

    public void setFrdbzjhm(String frdbzjhm) {
        this.frdbzjhm = frdbzjhm;
    }

    public String getKhgszzhm() {
        return khgszzhm;
    }

    public void setKhgszzhm(String khgszzhm) {
        this.khgszzhm = khgszzhm;
    }

    public String getGsnsh() {
        return gsnsh;
    }

    public void setGsnsh(String gsnsh) {
        this.gsnsh = gsnsh;
    }

    public String getDsnsh() {
        return dsnsh;
    }

    public void setDsnsh(String dsnsh) {
        this.dsnsh = dsnsh;
    }

    public String getDbrlxdh() {
        return dbrlxdh;
    }

    public void setDbrlxdh(String dbrlxdh) {
        this.dbrlxdh = dbrlxdh;
    }

    public String getWyzhm() {
        return wyzhm;
    }

    public void setWyzhm(String wyzhm) {
        this.wyzhm = wyzhm;
    }

    public Date getWyblrq() {
        return wyblrq;
    }

    public void setWyblrq(Date wyblrq) {
        this.wyblrq = wyblrq;
    }

    public String getWykhwd() {
        return wykhwd;
    }

    public void setWykhwd(String wykhwd) {
        this.wykhwd = wykhwd;
    }

    public String getWykhwddm() {
        return wykhwddm;
    }

    public void setWykhwddm(String wykhwddm) {
        this.wykhwddm = wykhwddm;
    }

    public String getWykhwdszd() {
        return wykhwdszd;
    }

    public void setWykhwdszd(String wykhwdszd) {
        this.wykhwdszd = wykhwdszd;
    }

    public String getSjyhzhm() {
        return sjyhzhm;
    }

    public void setSjyhzhm(String sjyhzhm) {
        this.sjyhzhm = sjyhzhm;
    }

    public Date getSjyhblrq() {
        return sjyhblrq;
    }

    public void setSjyhblrq(Date sjyhblrq) {
        this.sjyhblrq = sjyhblrq;
    }

    public String getSjyhkhwd() {
        return sjyhkhwd;
    }

    public void setSjyhkhwd(String sjyhkhwd) {
        this.sjyhkhwd = sjyhkhwd;
    }

    public String getSjyhkhwddm() {
        return sjyhkhwddm;
    }

    public void setSjyhkhwddm(String sjyhkhwddm) {
        this.sjyhkhwddm = sjyhkhwddm;
    }

    public String getSjyhkhwdszd() {
        return sjyhkhwdszd;
    }

    public void setSjyhkhwdszd(String sjyhkhwdszd) {
        this.sjyhkhwdszd = sjyhkhwdszd;
    }

    public Integer getLjjybs() {
        return ljjybs;
    }

    public void setLjjybs(Integer ljjybs) {
        this.ljjybs = ljjybs;
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
