package com.anluy.admin.eqa.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/5/8.
 */
public class EqaIndex {
    private String indexName;
    private String indexNameCn;
    private int sort;
    private final List<EqaMeta> eqaMetas = new ArrayList<>();

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getIndexNameCn() {
        return indexNameCn;
    }

    public void setIndexNameCn(String indexNameCn) {
        this.indexNameCn = indexNameCn;
    }

    public List<EqaMeta> getEqaMetas() {
        return eqaMetas;
    }

    public void addEqaMeta(EqaMeta eqaMeta) {
        eqaMetas.add(eqaMeta);
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
