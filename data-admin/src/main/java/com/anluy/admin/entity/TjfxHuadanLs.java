package com.anluy.admin.entity;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/5/26.
 */
public class TjfxHuadanLs extends TjfxHuadan {

    private Integer ddsl;//对端数量
    private Integer qtcs; //其他次数

    public Integer getDdsl() {
        return ddsl;
    }

    public void setDdsl(Integer ddsl) {
        this.ddsl = ddsl;
    }

    public Integer getQtcs() {
        return qtcs;
    }

    public void setQtcs(Integer qtcs) {
        this.qtcs = qtcs;
    }
}
