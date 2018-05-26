package com.anluy.admin.eqa.entity;

import com.anluy.commons.BaseEntity;

/**
 * 功能说明：元数据
 * <p>
 * Created by hc.zeng on 2018/3/26.
 */
public class EqaMeta  {
    private int id;
    private String indexName;
    private String fieldName;
    private String fieldCode;
    private String fieldType;
    private int isFx;
    private int sort;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public int getIsFx() {
        return isFx;
    }

    public void setIsFx(int isFx) {
        this.isFx = isFx;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
