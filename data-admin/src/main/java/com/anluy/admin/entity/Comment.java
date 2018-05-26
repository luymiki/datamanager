package com.anluy.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.anluy.commons.BaseEntity;

import java.util.Date;

/**
 * 功能说明：批注信息
 * <p>
 * Created by hc.zeng on 2018/5/18.
 */
public class Comment extends BaseEntity<String> {

    /**
     * 索引名称
     */
    @JSONField(name = "index_name")
    private String indexName;
    /**
     * 标签
     */
    private String tags;
    /**
     * 数据源id
     */
    private String source;
    /**
     * 批注内容
     */
    private String comment;

    /**
     * 创建时间
     */
    @JSONField(name = "create_time",format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
