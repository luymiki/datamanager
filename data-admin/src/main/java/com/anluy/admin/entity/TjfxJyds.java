package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * 功能说明：银行交易对手统计分析
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class TjfxJyds extends TjfxJyls implements Cloneable{

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JSONField(name = "df_id")
    private String dfId;// 交易对方银行账号

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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
