package com.anluy.admin.eqa.core;

import com.anluy.admin.eqa.entity.EqaIndex;
import com.anluy.admin.eqa.entity.EqaMeta;

import java.util.*;

/**
 * 功能说明：元数据映射
 * <p>
 * Created by hc.zeng on 2018/3/26.
 */
public class EqaMetaMap {
    final Map<String,EqaIndex> map = new LinkedHashMap<>();

    public EqaIndex getEqaMetaList(String indexName){
        return map.get(indexName);
    }

    public EqaMetaMap addEqaMeta(EqaIndex index,EqaMeta eqaMeta){
        if(map.containsKey(index.getIndexName())){
            map.get(index.getIndexName()).addEqaMeta(eqaMeta);
        }else {
            index.addEqaMeta(eqaMeta);
            map.put(eqaMeta.getIndexName(),index);
        }
        return this;
    }
    public EqaMetaMap addEqaIndex(EqaIndex eqaIndex){
        if(!map.containsKey(eqaIndex.getIndexName())){
            map.put(eqaIndex.getIndexName(),eqaIndex);
        }
        return this;
    }
    public EqaMetaMap addEqaMeta(EqaMeta eqaMeta){
        if(map.containsKey(eqaMeta.getIndexName())){
            map.get(eqaMeta.getIndexName()).addEqaMeta(eqaMeta);
        }
        return this;
    }
    public Map<String,EqaIndex> getMapAll(){
        return map;
    }

    public List<EqaIndex> getListAll(){
        List<EqaIndex> lists = new ArrayList<>();
        map.forEach((k,v)->{
            lists.add(v);
        });
        return lists;
    }
}
