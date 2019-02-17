package com.anluy.admin.web.neo4j;

import org.neo4j.graphdb.Label;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2019/2/16.
 */
public interface Labels {
    /**
     * 人员
     */
    Label PERSON = Label.label("PERSON");

    /**
     * 身份证节点
     */
    Label IDCARD_NODE = Label.label("IDCARD_NODE");

    /**
     * 身份证
     */
    Label IDCARD = Label.label("IDCARD");

    /**
     * QQ节点
     */
    Label QQ_NODE = Label.label("QQ_NODE");
    /**
     * QQ
     */
    Label QQ = Label.label("QQ");
    /**
     * Weixin节点
     */
    Label WEIXIN_NODE = Label.label("WEIXIN_NODE");
    /**
     * Weixin
     */
    Label WEIXIN = Label.label("WEIXIN");

    /**
     * 财付通TENPLAY节点
     */
    Label TENPLAY_NODE = Label.label("TENPLAY_NODE");
    /**
     * 财付通TENPLAY
     */
    Label TENPLAY = Label.label("TENPLAY");

    /**
     *支付宝ALIPLAY节点
     */
    Label ALIPLAY_NODE = Label.label("ALIPLAY_NODE");
    /**
     *支付宝ALIPLAY
     */
    Label ALIPLAY = Label.label("ALIPLAY");

    /**
     *银行账户节点
     */
    Label YHZH_NODE = Label.label("YHZH_NODE");

    /**
     *银行账户节点
     */
    Label YHZH = Label.label("YHZH");

    /**
     *手机节点
     */
    Label PHONE_NODE = Label.label("PHONE_NODE");
    /**
     *手机
     */
    Label PHONE = Label.label("PHONE");

    /**
     *IP节点
     */
    Label IP_NODE = Label.label("IP_NODE");
    /**
     *IP
     */
    Label IP = Label.label("IP");

    /**
     *EMAIL节点
     */
    Label EMAIL_NODE = Label.label("EMAIL_NODE");
    /**
     *EMAIL节点
     */
    Label EMAIL = Label.label("EMAIL");

}
