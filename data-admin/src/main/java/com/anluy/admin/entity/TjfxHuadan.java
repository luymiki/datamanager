package com.anluy.admin.entity;

import com.anluy.commons.BaseEntity;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/5/26.
 */
public class TjfxHuadan extends BaseEntity<String> {
    private Integer zjcs; //主叫次数
    private Integer zjthsc;//主叫总通话时长
    private Integer zjzcthsc;//主叫最长通话时长
    private Integer zjzdthsc;//主叫最短通话时长

    private Integer bjcs; //被叫次数
    private Integer bjthsc;//被叫总通话时长
    private Integer bjzcthsc;//被叫最长通话时长
    private Integer bjzdthsc;//被叫最短通话时长

    private Integer zthcs;//总通话次数
    private Integer zthsc;//总通话时长
    private Integer zcthsc;//最长通话时长
    private Integer zdthsc;//最短通话时长

    public Integer getZjcs() {
        return zjcs;
    }

    public void setZjcs(Integer zjcs) {
        this.zjcs = zjcs;
    }

    public Integer getZjzcthsc() {
        return zjzcthsc;
    }

    public void setZjzcthsc(Integer zjzcthsc) {
        this.zjzcthsc = zjzcthsc;
    }

    public Integer getZjzdthsc() {
        return zjzdthsc;
    }

    public void setZjzdthsc(Integer zjzdthsc) {
        this.zjzdthsc = zjzdthsc;
    }

    public Integer getBjcs() {
        return bjcs;
    }

    public void setBjcs(Integer bjcs) {
        this.bjcs = bjcs;
    }

    public Integer getBjzcthsc() {
        return bjzcthsc;
    }

    public void setBjzcthsc(Integer bjzcthsc) {
        this.bjzcthsc = bjzcthsc;
    }

    public Integer getBjzdthsc() {
        return bjzdthsc;
    }

    public void setBjzdthsc(Integer bjzdthsc) {
        this.bjzdthsc = bjzdthsc;
    }

    public Integer getZthsc() {
        return zthsc;
    }

    public void setZthsc(Integer zthsc) {
        this.zthsc = zthsc;
    }

    public Integer getZcthsc() {
        return zcthsc;
    }

    public void setZcthsc(Integer zcthsc) {
        this.zcthsc = zcthsc;
    }

    public Integer getZdthsc() {
        return zdthsc;
    }

    public void setZdthsc(Integer zdthsc) {
        this.zdthsc = zdthsc;
    }

    public Integer getZjthsc() {
        return zjthsc;
    }

    public void setZjthsc(Integer zjthsc) {
        this.zjthsc = zjthsc;
    }

    public Integer getBjthsc() {
        return bjthsc;
    }

    public void setBjthsc(Integer bjthsc) {
        this.bjthsc = bjthsc;
    }

    public Integer getZthcs() {
        return zthcs;
    }

    public void setZthcs(Integer zthcs) {
        this.zthcs = zthcs;
    }
}
