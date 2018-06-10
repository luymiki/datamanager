package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * 功能说明：支付宝交易对手统计分析
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class TjfxZfbJyds extends TjfxJyls {

    @JSONField(name = "user_id")
    private String userId;//支付宝账号

    @JSONField(name = "xcbh")
    private String xcbh;//

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JSONField(name = "df_id")
    private String dfId;// 交易对方支付宝账号

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDfId() {
        return dfId;
    }

    public void setDfId(String dfId) {
        this.dfId = dfId;
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
