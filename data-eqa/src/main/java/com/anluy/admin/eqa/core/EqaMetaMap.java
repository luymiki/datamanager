package com.anluy.admin.eqa.core;

import com.anluy.admin.eqa.entity.EqaMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/26.
 */
public class EqaMetaMap {
    final Map<String,List<EqaMeta>> map = new HashMap<>();

    public List<EqaMeta> getEqaMetaList(String indexName){
        return map.get(indexName);
    }
    public EqaMetaMap addEqaMeta(EqaMeta eqaMeta){
        if(map.containsKey(eqaMeta.getIndexName())){
            map.get(eqaMeta.getIndexName()).add(eqaMeta);
        }else {
            List<EqaMeta> sl = new ArrayList<>();
            sl.add(eqaMeta);
            map.put(eqaMeta.getIndexName(),sl);
        }
        return this;
    }
}
