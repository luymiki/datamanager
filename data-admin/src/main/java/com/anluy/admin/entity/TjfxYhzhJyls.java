package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * 功能说明：银行交易流水统计分析
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class TjfxYhzhJyls extends TjfxJyls {

    private String ssyh;//所属银行
    private String zh;//账号
    private String kh;//卡号

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSsyh() {
        return ssyh;
    }

    public void setSsyh(String ssyh) {
        this.ssyh = ssyh;
    }

    public String getZh() {
        return zh;
    }

    public void setZh(String zh) {
        this.zh = zh;
    }

    public String getKh() {
        return kh;
    }

    public void setKh(String kh) {
        this.kh = kh;
    }
}
