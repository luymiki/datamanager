package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * 功能说明：支付宝交易对手统计分析
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class TjfxZfbJyds extends TjfxJyds {

    @JSONField(name = "user_id")
    private String userId;//支付宝账号

    @JSONField(name = "xcbh")
    private String xcbh;//

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getXcbh() {
        return xcbh;
    }

    public void setXcbh(String xcbh) {
        this.xcbh = xcbh;
    }
}
