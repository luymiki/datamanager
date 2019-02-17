package com.anluy.admin.web.neo4j;

import org.neo4j.graphdb.Node;

import java.util.Map;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2019/2/16.
 */
public class SuspiciousNode {
    private Map info;
    private Node node;
    private Node idcardNode;
    private Node qqNode;
    private Node weixinNode;
    private Node tenplayNode;
    private Node aliplayNode;
    private Node yhzhNode;
    private Node phoneNode;
    private Node ipNode;
    private Node emailNode;

    public Map getInfo() {
        return info;
    }

    public void setInfo(Map info) {
        this.info = info;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Node getIdcardNode() {
        return idcardNode;
    }

    public void setIdcardNode(Node idcardNode) {
        this.idcardNode = idcardNode;
    }

    public Node getQqNode() {
        return qqNode;
    }

    public void setQqNode(Node qqNode) {
        this.qqNode = qqNode;
    }

    public Node getWeixinNode() {
        return weixinNode;
    }

    public void setWeixinNode(Node weixinNode) {
        this.weixinNode = weixinNode;
    }

    public Node getTenplayNode() {
        return tenplayNode;
    }

    public void setTenplayNode(Node tenplayNode) {
        this.tenplayNode = tenplayNode;
    }

    public Node getAliplayNode() {
        return aliplayNode;
    }

    public void setAliplayNode(Node aliplayNode) {
        this.aliplayNode = aliplayNode;
    }

    public Node getYhzhNode() {
        return yhzhNode;
    }

    public void setYhzhNode(Node yhzhNode) {
        this.yhzhNode = yhzhNode;
    }

    public Node getPhoneNode() {
        return phoneNode;
    }

    public void setPhoneNode(Node phoneNode) {
        this.phoneNode = phoneNode;
    }

    public Node getIpNode() {
        return ipNode;
    }

    public void setIpNode(Node ipNode) {
        this.ipNode = ipNode;
    }

    public Node getEmailNode() {
        return emailNode;
    }

    public void setEmailNode(Node emailNode) {
        this.emailNode = emailNode;
    }
}
