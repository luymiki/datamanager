package com.anluy.admin.entity;

import java.util.Date;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/5/26.
 */
public class TjfxHuadanDs extends TjfxHuadan {

    private String ddhm;//对端电话
    private Date zzthsj;//最早通话时间
    private Date zwthsj;//最晚通话时间

    public Date getZzthsj() {
        return zzthsj;
    }

    public void setZzthsj(Date zzthsj) {
        this.zzthsj = zzthsj;
    }

    public Date getZwthsj() {
        return zwthsj;
    }

    public void setZwthsj(Date zwthsj) {
        this.zwthsj = zwthsj;
    }

    public String getDdhm() {
        return ddhm;
    }

    public void setDdhm(String ddhm) {
        this.ddhm = ddhm;
    }
}
