package com.anluy.admin.entity;

import com.anluy.commons.BaseEntity;

import java.util.Date;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/5/23.
 */
public class TjfxJyls extends BaseEntity<String> {
    private Double zdjyje;//最大交易金额
    private Double zxjyje;//最小交易金额
    private Double zdzrje;//最大转入金额
    private Double zdzcje;//最大转出金额
    private Double zxzrje;//最小转入金额
    private Double zxzcje;//最小转出金额
    private Double ljjyje;//累计交易金额
    private Double ljzrje;//累计转入金额
    private Double ljzcje;//累计转出金额
    private int ljjybs;//累计交易笔数
    private int ljzrbs;//累计转入笔数
    private int ljzcbs;//累计转出笔数
    private Double pjjyje;//平均交易金额
//    private Double zzzdjyje;//转账最大交易金额
//    private Double zzzxjyje;//转账最小交易金额
//    private Double zzzdzrje;//转账最大转入金额
//    private Double zzzdzcje;//转账最大转出金额
//    private Double zzzxzrje;//转账最小转入金额
//    private Double zzzxzcje;//转账最小转出金额
//    private Double zzljjyje;//转账累计交易金额
//    private Double zzljzcje;//转账累计转出金额
//    private Double zzljzrje;//转账累计转入金额
//    private int zzljjybs;//转账累计交易笔数
//    private int zzljzrbs;//转账累计转入笔数
//    private int zzljzcbs;//转账累计转出笔数
//    private Double zzpjjyje;//转账平均交易金额
    private Date zzjysj;//最早交易时间
    private Date zwjysj;//最晚交易时间

    public Double getZdjyje() {
        return zdjyje;
    }

    public void setZdjyje(Double zdjyje) {
        this.zdjyje = zdjyje;
    }

    public Double getZxjyje() {
        return zxjyje;
    }

    public void setZxjyje(Double zxjyje) {
        this.zxjyje = zxjyje;
    }

    public Double getZdzrje() {
        return zdzrje;
    }

    public void setZdzrje(Double zdzrje) {
        this.zdzrje = zdzrje;
    }

    public Double getZdzcje() {
        return zdzcje;
    }

    public void setZdzcje(Double zdzcje) {
        this.zdzcje = zdzcje;
    }

    public Double getZxzrje() {
        return zxzrje;
    }

    public void setZxzrje(Double zxzrje) {
        this.zxzrje = zxzrje;
    }

    public Double getZxzcje() {
        return zxzcje;
    }

    public void setZxzcje(Double zxzcje) {
        this.zxzcje = zxzcje;
    }

    public Double getLjjyje() {
        return ljjyje;
    }

    public void setLjjyje(Double ljjyje) {
        this.ljjyje = ljjyje;
    }

    public int getLjjybs() {
        return ljjybs;
    }

    public void setLjjybs(int ljjybs) {
        this.ljjybs = ljjybs;
    }

    public int getLjzrbs() {
        return ljzrbs;
    }

    public void setLjzrbs(int ljzrbs) {
        this.ljzrbs = ljzrbs;
    }

    public int getLjzcbs() {
        return ljzcbs;
    }

    public void setLjzcbs(int ljzcbs) {
        this.ljzcbs = ljzcbs;
    }

    public Double getPjjyje() {
        return pjjyje;
    }

    public void setPjjyje(Double pjjyje) {
        this.pjjyje = pjjyje;
    }
//
//    public Double getZzzdjyje() {
//        return zzzdjyje;
//    }
//
//    public void setZzzdjyje(Double zzzdjyje) {
//        this.zzzdjyje = zzzdjyje;
//    }
//
//    public Double getZzzxjyje() {
//        return zzzxjyje;
//    }
//
//    public void setZzzxjyje(Double zzzxjyje) {
//        this.zzzxjyje = zzzxjyje;
//    }
//
//    public Double getZzzdzrje() {
//        return zzzdzrje;
//    }
//
//    public void setZzzdzrje(Double zzzdzrje) {
//        this.zzzdzrje = zzzdzrje;
//    }
//
//    public Double getZzzdzcje() {
//        return zzzdzcje;
//    }
//
//    public void setZzzdzcje(Double zzzdzcje) {
//        this.zzzdzcje = zzzdzcje;
//    }
//
//    public Double getZzzxzrje() {
//        return zzzxzrje;
//    }
//
//    public void setZzzxzrje(Double zzzxzrje) {
//        this.zzzxzrje = zzzxzrje;
//    }
//
//    public Double getZzzxzcje() {
//        return zzzxzcje;
//    }
//
//    public void setZzzxzcje(Double zzzxzcje) {
//        this.zzzxzcje = zzzxzcje;
//    }
//
//    public Double getZzljjyje() {
//        return zzljjyje;
//    }
//
//    public void setZzljjyje(Double zzljjyje) {
//        this.zzljjyje = zzljjyje;
//    }
//
//    public int getZzljjybs() {
//        return zzljjybs;
//    }
//
//    public void setZzljjybs(int zzljjybs) {
//        this.zzljjybs = zzljjybs;
//    }
//
//    public int getZzljzrbs() {
//        return zzljzrbs;
//    }
//
//    public void setZzljzrbs(int zzljzrbs) {
//        this.zzljzrbs = zzljzrbs;
//    }
//
//    public int getZzljzcbs() {
//        return zzljzcbs;
//    }
//
//    public void setZzljzcbs(int zzljzcbs) {
//        this.zzljzcbs = zzljzcbs;
//    }
//
//    public Double getZzpjjyje() {
//        return zzpjjyje;
//    }
//
//    public void setZzpjjyje(Double zzpjjyje) {
//        this.zzpjjyje = zzpjjyje;
//    }

    public Date getZzjysj() {
        return zzjysj;
    }

    public void setZzjysj(Date zzjysj) {
        this.zzjysj = zzjysj;
    }

    public Date getZwjysj() {
        return zwjysj;
    }

    public void setZwjysj(Date zwjysj) {
        this.zwjysj = zwjysj;
    }

    public Double getLjzrje() {
        return ljzrje;
    }

    public void setLjzrje(Double ljzrje) {
        this.ljzrje = ljzrje;
    }

    public Double getLjzcje() {
        return ljzcje;
    }

    public void setLjzcje(Double ljzcje) {
        this.ljzcje = ljzcje;
    }
//
//    public Double getZzljzcje() {
//        return zzljzcje;
//    }
//
//    public void setZzljzcje(Double zzljzcje) {
//        this.zzljzcje = zzljzcje;
//    }
//
//    public Double getZzljzrje() {
//        return zzljzrje;
//    }
//
//    public void setZzljzrje(Double zzljzrje) {
//        this.zzljzrje = zzljzrje;
//    }
}
