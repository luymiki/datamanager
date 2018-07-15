package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * 功能说明：银行交易对手统计分析
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class TjfxYhzhJyds extends TjfxJyds {

    private String ssyh;//所属银行
    private String zh;//账号
    private String kh;//卡号

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
