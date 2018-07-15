package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;

/**
 * 功能说明：财付通交易对手统计分析
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class TjfxCftJyds  extends TjfxJyds {

    @JSONField(name = "cft_id")
    private String cftId;// 财付通账号

    public String getCftId() {
        return cftId;
    }

    public void setCftId(String cftId) {
        this.cftId = cftId;
    }

}
