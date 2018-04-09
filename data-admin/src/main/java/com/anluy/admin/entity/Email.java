package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.*;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/25.
 */
public class Email extends BaseEntity<String> {
    /**
     * 发送人
     **/
    private String from;
    /**
     * 发送地址
     **/
    @JSONField(name = "from_address")
    private String fromAddress;
    /**
     * 接收人
     **/
    private String to;
    /**
     * 接收地址
     **/
    @JSONField(name = "to_address")
    private String toAddress;

    /**
     * 接收时间
     */
    @JSONField(name = "received_date", format = "yyyy-MM-dd HH:mm:ss")
    private Date receivedDate;


    /**
     * 邮件主题
     **/
    private String subject;

    /**
     * 邮件内容
     **/
    private List<Object> content = new ArrayList<>();
    /**
     * 邮件附件内容
     **/
    @JSONField(name = "file_list")
    private List<Map> fileList = new ArrayList<>();

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JSONField(name = "file_id")
    private String fileId;

    @JSONField(name = "susp_id")
    private String suspId;

    @JSONField(name = "susp_name")
    private String suspName;

    private String tags;//标签

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<Map> getFileList() {
        return fileList;
    }

    public void setFileList(List<Map> fileList) {
        this.fileList = fileList;
    }

    public List<Object> getContent() {
        return content;
    }

    public void setContent(List<Object> content) {
        this.content = content;
    }

    public void addContent(Object content) {
        this.content.add(content);
    }

    public void addFile(String path, String contentType) {
        Map<String, String> file = new HashMap<>();
        file.put("path", path);
        file.put("type", contentType);
        fileList.add(file);
    }


    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
