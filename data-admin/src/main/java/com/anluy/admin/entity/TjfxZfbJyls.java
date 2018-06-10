package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * 功能说明：支付宝交易流水统计分析
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class TjfxZfbJyls extends TjfxJyls {

    @JSONField(name = "user_id")
    private String userId;//支付宝账号

    @JSONField(name = "xcbh")
    private String xcbh;//

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

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
