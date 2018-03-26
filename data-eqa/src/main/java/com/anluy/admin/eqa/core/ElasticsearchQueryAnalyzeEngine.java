package com.anluy.admin.eqa.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.eqa.entity.EqaMeta;
import com.anluy.commons.Configuration;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/18.
 */
@Component
public class ElasticsearchQueryAnalyzeEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchQueryAnalyzeEngine.class);

    @Resource
    EqaMetaMap eqaMetaMap ;

    /**
     * 统计类型
     **/
    private static final int AGGS_TYPE_TERMS = 1;//TERMS 完全匹配
    /**
     * 查询类型
     **/
    private static final int SEARCH_TYPE_EXACTLY = 1;//exactly 完全匹配
    private static final int SEARCH_TYPE_FUZZY = 2;//Fuzzy 模糊匹配
    private static final int SEARCH_TYPE_NOT_INCLUD = 3;//not includ 不包含
    /**
     * 字段类型
     **/
    private static final int DATA_TYPE_TEXT = 1;//文本
    private static final int DATA_TYPE_TAG = 2;//标签
    private static final int DATA_TYPE_NUM = 3;//数字 3;//数字
    private static final int DATA_TYPE_DATE = 4;//日期
    private static final int DATA_TYPE_DIC = 5;//字典


    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    private JSONObject createDsl(Integer pageNum, Integer pageSize) {
        JSONObject dsl = new JSONObject();
        dsl.put("size", pageSize == null ? 10 : pageSize);
        pageNum = pageNum == null ? 1 : pageNum;
        int from = (pageNum - 1) * pageSize;
        dsl.put("from", from);
        return dsl;
    }

    public Map query(String paramsStr, Integer pageNum, Integer pageSize) throws IOException {
        JSONObject dsl = createDsl(pageNum, pageSize);
        //query.put("match_all",new HashMap<>());
        //JSONObject bool = new JSONObject();
        //JSONArray must = new JSONArray();
        //JSONObject should = new JSONObject();
        //JSONObject must_not = new JSONObject();
        //query.put("bool", bool);
        //bool.put("must", must);
        //bool.put("should", should);
        //bool.put("must_not", must_not);

        Configuration root = Configuration.from(paramsStr);
        String sortVal = root.getString("sort");
        List sortList = this.sort(sortVal);
        if (sortList != null && !sortList.isEmpty()) {
            dsl.put("sort", sortList);
        }
        String indexName = root.getString("indexName");
        List<Configuration> conditionList = root.getListConfiguration("conditions");
        if (conditionList != null && !conditionList.isEmpty()) {
            JSONObject query = queryDsl(conditionList);
            dsl.put("query", query);
        }
        //bool.put("must", must);
//        for (Configuration condition : conditionList) {
//            Integer searchType = condition.getInt("searchType",SEARCH_TYPE_EXACTLY);
//            Integer dataType = condition.getInt("dataType",DATA_TYPE_TEXT);
//            String field = condition.getString("field");
//            List<String> values = condition.getList("values", String.class);
//            JSONObject itemBool = new JSONObject();
//            JSONObject items = new JSONObject();
//            itemBool.put("bool",items);
//            must.add(itemBool);
//            switch (searchType) {
//                case SEARCH_TYPE_EXACTLY: {
//                    items.put("should",this.condition(field,values,dataType,false));
//                    break;
//                }
//                case SEARCH_TYPE_FUZZY: {
//                    items.put("should",this.condition(field,values,dataType,true));
//                    break;
//                }
//                case SEARCH_TYPE_NOT_INCLUD: {
//                    items.put("must_not",this.condition(field,values,dataType,true));
//                    break;
//                }
//            }
//
//        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(dsl.toJSONString());
        }
        Map result = elasticsearchRestClient.query(dsl.toJSONString(), indexName);
        return  this.setMeta(result,indexName);
    }

    public Map aggs(String paramsStr,Integer pageNum, Integer pageSize) throws IOException {
        JSONObject dsl = createDsl(pageNum, pageSize);
        //JSONArray aggs = new JSONArray();
        //dsl.put("aggs",aggs);
        Configuration root = Configuration.from(paramsStr);
        String indexName = root.getString("indexName");
        List<Configuration> conditionList = root.getListConfiguration("conditions");
        List<Configuration> aggsList = root.getListConfiguration("aggs");
        if (conditionList != null && !conditionList.isEmpty()) {
            JSONObject query = queryDsl(conditionList);
            dsl.put("query", query);
        }
        if (aggsList == null || aggsList.isEmpty()) {
            throw new IOException("聚合参数为空");
        }
        JSONObject aggs = aggsDsl(aggsList);
        dsl.put("aggregations", aggs);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(dsl.toJSONString());
        }
        return elasticsearchRestClient.aggs(dsl.toJSONString(), indexName);
    }

    private JSONObject queryDsl(List<Configuration> conditionList) {
        JSONObject query = new JSONObject();
        JSONObject bool = new JSONObject();
        query.put("bool", bool);
        JSONArray must = new JSONArray();
        for (Configuration condition : conditionList) {
            Integer searchType = condition.getInt("searchType", SEARCH_TYPE_EXACTLY);
            Integer dataType = condition.getInt("dataType", DATA_TYPE_TEXT);
            String field = condition.getString("field");
            List<String> values = condition.getList("values", String.class);
            JSONObject itemBool = new JSONObject();
            JSONObject items = new JSONObject();
            itemBool.put("bool", items);
            must.add(itemBool);
            switch (searchType) {
                case SEARCH_TYPE_EXACTLY: {
                    items.put("should", this.condition(field, values, dataType, false));
                    break;
                }
                case SEARCH_TYPE_FUZZY: {
                    items.put("should", this.condition(field, values, dataType, true));
                    break;
                }
                case SEARCH_TYPE_NOT_INCLUD: {
                    items.put("must_not", this.condition(field, values, dataType, true));
                    break;
                }
            }
        }
        bool.put("must", must);
        return query;
    }

    private JSONObject aggsDsl(List<Configuration> aggsList) {
        JSONObject group = new JSONObject();
        for (Configuration ag : aggsList) {
            String groupName = "gp-" + UUID.randomUUID().toString();
            Integer aggsType = ag.getInt("aggsType", AGGS_TYPE_TERMS);
            Integer dataType = ag.getInt("dataType", DATA_TYPE_TEXT);
            String field = ag.getString("field");
            switch (aggsType) {
                case AGGS_TYPE_TERMS: {
                    group.put(groupName, this.aggs(groupName, field, dataType));
                    break;
                }
            }
        }
        return group;
    }

    private Map condition(String field, String value, int dataType, boolean fuzzy) {
        Map conditionMap = new HashMap();
        Map map = new HashMap();

        switch (dataType) {
            case DATA_TYPE_TEXT:
            case DATA_TYPE_DIC: {
                if (fuzzy) {
                    map.put(field, "*" + value + "*");
                } else {
                    map.put(field, value);
                }
                conditionMap.put("wildcard", map);
                break;
            }
            case DATA_TYPE_TAG: {
                map.put(field, value);
                conditionMap.put("term", map);
                break;
            }
        }
        ;

        return conditionMap;
    }

    private List<Map> condition(String field, List<String> values, int dataType, boolean fuzzy) {
        List<Map> conditions = new ArrayList<>();
        for (String val : values) {
            conditions.add(this.condition(field, val, dataType, fuzzy));
        }
        return conditions;
    }

    private Map aggs(String aggsGroupName, String field, int dataType) {
        Map<String, Object> item = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        map.put("field", field);
        switch (dataType) {
            case DATA_TYPE_TEXT:
            case DATA_TYPE_DIC: {
                item.put("terms", map);
                break;
            }
            case DATA_TYPE_TAG: {
                item.put("terms", map);
                break;
            }
        }
        ;
        return item;
    }

    private List sort(String sortVal) {
        if (StringUtils.isNotBlank(sortVal)) {
            List<Map> sortItem = new ArrayList<>();
            String[] ss = sortVal.trim().split(",");
            for (String s : ss) {
                String[] sr = s.trim().split(" ");
                Map item = new HashMap();
                Map order = new HashMap();
                String o = sr.length >= 1 ? sr[1] : "asc".toLowerCase();
                if (!"asc".equals(o) && !"desc".equals(o)) {
                    o = "asc";
                }
                order.put("order", o);
                item.put(sr[0], order);
                sortItem.add(item);
            }
            return sortItem;
        }
        return null;
    }

    /**
     * 全文检索
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     * @throws IOException
     */
    public Map fulltext(String keyword, Integer pageNum, Integer pageSize) throws IOException {
        JSONObject dsl = createDsl(pageNum, pageSize);
        JSONObject query = new JSONObject();
        JSONObject queryString = new JSONObject();
        queryString.put("query",keyword);
        query.put("query_string",queryString);
        dsl.put("query", query);
        LOGGER.info(dsl.toJSONString());
        Map result = elasticsearchRestClient.query(dsl.toJSONString(), null);
        return  this.setMetaList(result);
    }


    /**
     * 往数据结果里设置元数据信息
     * @param result
     * @return
     */
    private Map setMetaList(Map result){
        //result.put("meta",new HashMap<>());
        if(result.containsKey("data")){
            Object data = result.get("data");
            if(data instanceof List){
                ((List<Map>)data).forEach(map->{
                    String index = (String) map.get("_index");
                    this.setMeta(result,index);
                });
            }else if(data instanceof Map){
                String index = (String) ((Map)data).get("_index");
                this.setMeta(result,index);
            }
        }
        return result;
    }
    private Map setMeta(Map result,String indexName){
        if(StringUtils.isNotBlank(indexName)){
            List<EqaMeta> list = eqaMetaMap.getEqaMetaList(indexName);
            if(list!=null){
                if(result.get("meta") == null){
                    result.put("meta",new HashMap<>());
                }
                if(!((Map)result.get("meta")).containsKey(indexName)){
                    ((Map)result.get("meta")).put(indexName,list);
                }
            }
        }
        return result;
    }

}
