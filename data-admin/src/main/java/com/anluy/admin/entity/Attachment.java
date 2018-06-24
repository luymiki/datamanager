package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/19.
 */
public class Attachment extends BaseEntity<String> {

    /**
     * 文件夹
     */
    private String folder;
    /**
     * 标签
     */
    private String tags;
    /**
     * 文件路径
     */
    private String path;
    /**
     * 文件名称
     */
    private String name;
    /**
     * 文件大小
     */
    private long size;
    /**
     * 文件后缀
     */
    private String suffix;
    /**
     * 文件类型
     */
    private String type;
    /**
     * 创建时间
     */
    @JSONField(name = "create_time",format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 文件所属嫌疑人姓名
     */
    @JSONField(name = "susp_name")
    private String suspName;

    /**
     * 文件所属嫌疑人id
     */
    @JSONField(name = "susp_id")
    private String suspId;

    /**
     * 文件MD5
     */
    @JSONField(name = "md5")
    private String md5;

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSuspName() {
        return suspName;
    }

    public void setSuspName(String suspName) {
        this.suspName = suspName;
    }

    public String getSuspId() {
        return suspId;
    }

    public void setSuspId(String suspId) {
        this.suspId = suspId;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
