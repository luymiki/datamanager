package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;
import java.util.List;

/**
 * 功能说明：支付宝登录信息
 * <p>
 * Created by hc.zeng on 2018/4/21.
 */
public class ZfbLoginInfo extends BaseEntity<String> {
    @JSONField(name = "file_id")
    private String fileId;

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String tags;//标签
    @JSONField(name = "susp_id")
    private String suspId;

    @JSONField(name = "susp_name")
    private String suspName;
    @JSONField(name = "user_id")
    private String userId;//用户Id
    private String dlzh;//登陆账号
    private String name;//账户名称
    private String ip;//客户端ip
    private String gsd;//ip归属地
    @JSONField(name = "czsj", format = "yyyy-MM-dd HH:mm:ss")
    private Date czsj;//操作发生时间
    private String xcbh;//对应的协查数据

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDlzh() {
        return dlzh;
    }

    public void setDlzh(String dlzh) {
        this.dlzh = dlzh;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getCzsj() {
        return czsj;
    }

    public void setCzsj(Date czsj) {
        this.czsj = czsj;
    }

    public String getXcbh() {
        return xcbh;
    }

    public void setXcbh(String xcbh) {
        this.xcbh = xcbh;
    }

    public String getGsd() {
        return gsd;
    }

    public void setGsd(String gsd) {
        this.gsd = gsd;
    }

    public String getSuspId() {
        return suspId;
    }

    public void setSuspId(String suspId) {
        this.suspId = suspId;
    }

    public String getSuspName() {
        return suspName;
    }

    public void setSuspName(String suspName) {
        this.suspName = suspName;
    }
}
