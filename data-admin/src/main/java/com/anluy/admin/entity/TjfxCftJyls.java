package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;

/**
 * 功能说明：财付通交易流水统计分析
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class TjfxCftJyls extends TjfxJyls {

    @JSONField(name = "cft_id")
    private String cftId;//财付通账号

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCftId() {
        return cftId;
    }

    public void setCftId(String cftId) {
        this.cftId = cftId;
    }

}
