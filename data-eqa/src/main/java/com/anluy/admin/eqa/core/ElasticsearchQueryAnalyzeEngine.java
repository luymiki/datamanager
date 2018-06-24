package com.anluy.admin.eqa.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.eqa.entity.EqaIndex;
import com.anluy.admin.eqa.entity.EqaMeta;
import com.anluy.commons.Configuration;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    EqaMetaMap eqaMetaMap;

    /*** 统计类型**/
    private static final int AGGS_TYPE_TERMS = 1;//TERMS 分组求COUNT
    private static final int AGGS_TYPE_MAX = 2;//MAX 最大值
    private static final int AGGS_TYPE_MIN = 3;//MIN 最小值
    private static final int AGGS_TYPE_AVG = 4;//avg 平均值
    private static final int AGGS_TYPE_SUM = 5;//sum 求和
    private static final int AGGS_TYPE_CARDINALITY = 6;//cardinality 值去重计数
    private static final int AGGS_TYPE_COUNT = 7;//value_count 全数据计数
    private static final int AGGS_TYPE_RANGE = 8;//RANGE 范围计数

    /**
     * 查询类型
     **/
    private static final int SEARCH_TYPE_EXACTLY = 1;//exactly 完全匹配
    private static final int SEARCH_TYPE_FUZZY = 2;//Fuzzy 模糊匹配
    private static final int SEARCH_TYPE_NOT_INCLUD = 3;//not includ 不包含
    private static final int SEARCH_TYPE_GT = 4;//range 范围查询大于
    private static final int SEARCH_TYPE_LT = 5;//range 范围查询小于
    private static final int SEARCH_TYPE_RANGE = 6;//range 范围查询
    /**
     * 字段类型
     **/
    private static final int DATA_TYPE_TEXT = 1;//文本
    private static final int DATA_TYPE_TAG = 2;//标签
    private static final int DATA_TYPE_NUM = 3;//数字
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
//        LOGGER.info("ES返回数据"+ JSON.toJSONString(result));
        return this.setMeta(result, indexName);
    }

    private JSONObject queryDsl(List<Configuration> conditionList) {
        JSONObject query = new JSONObject();
        JSONObject bool = new JSONObject();
        query.put("bool", bool);
        JSONArray must = new JSONArray();

        Map<String, List<Configuration>> groupMap = new HashMap<>();
        List<Configuration> mustList = new ArrayList<>();
        List<Configuration> notList = new ArrayList<>();
        //分组
        for (Configuration condition : conditionList) {
            String groupType = condition.getString("groupType", "must");
            String groupId = condition.getString("groupId");
            //如果有分组，
            // 或者的关系
            if (StringUtils.isNotBlank(groupId)) {
                //groupType = "should";
                if (!groupMap.containsKey(groupId)) {
                    List<Configuration> shouldList = new ArrayList<>();
                    groupMap.put(groupId, shouldList);
                }
                groupMap.get(groupId).add(condition);
            } else if ("not".equals(groupType)) {//不包含
                notList.add(condition);
            } else {//包含
                mustList.add(condition);
            }
        }
        if (!mustList.isEmpty()) {
            must.add(this.must(mustList));
        }
        if (!groupMap.isEmpty()) {
            must.add(this.should(groupMap));
        }
        if (!notList.isEmpty()) {
            must.add(this.not(notList));
        }
//
//
//
//
//        for (Configuration condition : conditionList) {
//            Integer searchType = condition.getInt("searchType", SEARCH_TYPE_EXACTLY);
//            Integer dataType = condition.getInt("dataType", DATA_TYPE_TEXT);
//            String field = condition.getString("field");
//            List<String> values = condition.getList("values", String.class);
//            JSONObject itemBool = new JSONObject();
//            JSONObject items = new JSONObject();
//            itemBool.put("bool", items);
//            must.add(itemBool);
//            switch (searchType) {
//                case SEARCH_TYPE_EXACTLY: {
//                    items.put("should", this.condition(field, values, dataType, false));
//                    break;
//                }
//                case SEARCH_TYPE_FUZZY: {
//                    items.put("should", this.condition(field, values, dataType, true));
//                    break;
//                }
//                case SEARCH_TYPE_NOT_INCLUD: {
//                    items.put("must_not", this.condition(field, values, dataType, true));
//                    break;
//                }
//            }
//        }
        bool.put("must", must);
        return query;
    }

    private JSONObject should(Map<String, List<Configuration>> groupMap) {
        JSONObject query = new JSONObject();
        JSONObject bool = new JSONObject();
        query.put("bool", bool);
        JSONArray should = new JSONArray();
        bool.put("should", should);

        groupMap.forEach((g, conditionList) -> {
            should.add(must(conditionList));
//            JSONObject subquery = new JSONObject();
//            JSONObject subbool = new JSONObject();
//            subquery.put("bool", subbool);
//            JSONArray subshould = new JSONArray();
//            subbool.put("should",subshould);
//            this.dsl(conditionList,subshould);
        });

        return query;
    }

    private JSONObject must(List<Configuration> mustList) {
        JSONObject query = new JSONObject();
        JSONObject bool = new JSONObject();
        query.put("bool", bool);
        JSONArray must = new JSONArray();
        bool.put("must", must);
        this.dsl(mustList, must);
        return query;
    }

    private JSONObject not(List<Configuration> notList) {
        JSONObject query = new JSONObject();
        JSONObject bool = new JSONObject();
        query.put("bool", bool);
        JSONArray must_not = new JSONArray();
        bool.put("must_not", must_not);
        this.dsl(notList, must_not);
        return query;
    }

    private void dsl(List<Configuration> list, JSONArray item) {
        for (Configuration condition : list) {
            Integer searchType = condition.getInt("searchType", SEARCH_TYPE_EXACTLY);
            Integer dataType = condition.getInt("dataType", DATA_TYPE_TEXT);
            String field = condition.getString("field");
            List<String> values = condition.getList("values", String.class);
            if(values == null){
                continue;
            }
            JSONObject itemBool = new JSONObject();
            JSONObject items = new JSONObject();
            itemBool.put("bool", items);
            item.add(itemBool);
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
                    items.put("must_not", this.condition(field, values, dataType, false));
                    break;
                }
                case SEARCH_TYPE_GT:
                case SEARCH_TYPE_LT:
                case SEARCH_TYPE_RANGE: {
                    items.put("should", this.range(field, values, searchType, dataType));
                    break;
                }
            }
        }
    }


    private Map condition(String field, String value, int dataType, boolean fuzzy) {
        Map conditionMap = new HashMap();
        Map map = new HashMap();

        switch (dataType) {
            case DATA_TYPE_TEXT:{
                map.put("default_field",field);
                if (fuzzy) {
                    map.put("query", "*" + value + "*");
                } else {
                    map.put("query", value);
                }
                conditionMap.put("query_string", map);
                break;
            }
            case DATA_TYPE_DIC: {
                map.put(field, value);
                conditionMap.put("wildcard", map);
                break;
            }
            case DATA_TYPE_TAG:
            default: {
                if (fuzzy) {
                    map.put(field, "*" + value + "*");
                    conditionMap.put("wildcard", map);
                } else {
                    map.put(field, value);
                    conditionMap.put("term", map);
                }
                break;
            }

        }
        ;

        return conditionMap;
    }

    /**
     * f范围查询条件拼接
     *
     * @param field
     * @param values
     * @param dataType
     * @return
     */
    private List<Map> range(String field, List<String> values, int searchType, int dataType) {
        List<Map> conditions = new ArrayList<>();
        Map conditionMap = new HashMap();
        Map map = new HashMap();
        Map datamap = new HashMap();
        switch (searchType) {
            case SEARCH_TYPE_GT: {
                if (values.size() == 1) {
                    String start = values.get(0);
                    datamap.put("gt", start);
                    map.put(field, datamap);
                    conditionMap.put("range", map);
                }
                break;
            }
            case SEARCH_TYPE_LT: {
                if (values.size() == 1) {
                    String start = values.get(0);
                    datamap.put("lt", start);
                    map.put(field, datamap);
                    conditionMap.put("range", map);
                }
                break;
            }
            case SEARCH_TYPE_RANGE: {
                if (values.size() == 2) {
                    String start = values.get(0);
                    String end = values.get(1);
                    if (StringUtils.isNotBlank(start)) {
                        datamap.put("gte", start);
                    }
                    if (StringUtils.isNotBlank(end)) {
                        datamap.put("lt", end);
                    }
                    map.put(field, datamap);
                    conditionMap.put("range", map);
                }
                break;
            }
        }

        conditions.add(conditionMap);
        return conditions;
    }

    private List<Map> condition(String field, List<String> values, int dataType, boolean fuzzy) {
        List<Map> conditions = new ArrayList<>();
        for (String val : values) {
            if(StringUtils.isNotBlank(val)){
                conditions.add(this.condition(field, val, dataType, fuzzy));
            }
        }
        return conditions;
    }


    public Map aggs(String paramsStr, Integer pageNum, Integer pageSize) throws IOException {
        JSONObject dsl = createDsl(pageNum, 0);
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
        JSONObject aggs = this.aggsDsl(aggsList);
        dsl.put("aggregations", aggs);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(dsl.toJSONString());
        }
        return elasticsearchRestClient.aggs(dsl.toJSONString(), indexName);
    }

    /**
     * @param aggsList
     * @return
     */
    private JSONObject aggsDsl(List<Configuration> aggsList) {
        JSONObject group = new JSONObject();
        for (Configuration ag : aggsList) {
            String groupName = ag.getString("groupName");
            if (StringUtils.isBlank(groupName)) {
                groupName = "gp-" + UUID.randomUUID().toString();
            }
            Integer aggsType = ag.getInt("aggsType", AGGS_TYPE_TERMS);
//            Integer dataType = ag.getInt("dataType", DATA_TYPE_TEXT);
            String field = ag.getString("field");


            Map<String, Object> item = new HashMap<>();
            Map<String, Object> map = new HashMap<>();
            map.put("field", field);
            switch (aggsType) {
                case AGGS_TYPE_TERMS: {
                    item.put("terms", map);
                    map.put("size", 10000);
                    break;
                }
                case AGGS_TYPE_MAX: {
                    item.put("max", map);
                    break;
                }
                case AGGS_TYPE_MIN: {
                    item.put("min", map);
                    break;
                }
                case AGGS_TYPE_AVG: {
                    item.put("avg", map);
                    break;
                }
                case AGGS_TYPE_SUM: {
                    item.put("sum", map);
                    break;
                }
                case AGGS_TYPE_CARDINALITY: {
                    item.put("cardinality", map);
                    break;
                }
                case AGGS_TYPE_COUNT: {
                    item.put("value_count", map);
                    break;
                }
                case AGGS_TYPE_RANGE: {
                    List valueList = ag.getList("values");
                    if (valueList != null || !valueList.isEmpty()) {
                        List<Map> rangeList = new ArrayList<>();
                        Object one = valueList.get(0);
                        if(one instanceof Map){
                            for (Object obj : valueList) {
                                Map dm = (Map)obj;
                                Object from = dm.get("from");
                                Object to = dm.get("to");
                                Map<String, Object> range = new HashMap<>();
                                if(from!=null){
                                    range.put("from",from);
                                }
                                if(to!=null){
                                    range.put("to",to);
                                }
                                rangeList.add(range);
                            }
                        }else {
                            Object from = valueList.get(0);
                            Object to = null;
                            if(valueList.size()>1){
                                to = valueList.get(1);
                            }
                            Map<String, Object> range = new HashMap<>();
                            if(from!=null){
                                range.put("from",from);
                            }
                            if(to!=null){
                                range.put("to",to);
                            }
                            rangeList.add(range);
                        }
                        map.put("ranges",rangeList);
                        item.put("range", map);
                    }
                    break;
                }
            }

            group.put(groupName, item);
        }
        return group;
    }
//    private Map aggs(String aggsGroupName, String field, int dataType) {
//        Map<String, Object> item = new HashMap<>();
//        Map<String, Object> map = new HashMap<>();
//        map.put("field", field);
//        switch (dataType) {
//            case DATA_TYPE_TEXT:
//            case DATA_TYPE_DIC: {
//                item.put("terms", map);
//                break;
//            }
//            case DATA_TYPE_TAG: {
//                item.put("terms", map);
//                break;
//            }
//        }
//        map.put("size", 1000);
//        return item;
//    }

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
     *
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     * @throws IOException
     */
    public Map fulltext(String keyword, Integer pageNum, Integer pageSize, String indexName,String sort) throws IOException {
        JSONObject dsl = createDsl(pageNum, pageSize);
        JSONObject query = new JSONObject();
        JSONObject bool = new JSONObject();
        query.put("bool",bool);
        JSONArray must = new JSONArray();
        bool.put("must",must);
        String[] ss = keyword.split(" |,|，");
        for (String k : ss) {
            if(StringUtils.isBlank(k)){
                continue;
            }
            JSONObject queryString = new JSONObject();
            queryString.put("query","*"+k+"*");
            queryString.put("default_operator","AND");
            JSONObject queryItem = new JSONObject();
            queryItem.put("query_string", queryString);
            must.add(queryItem);
        }
        dsl.put("query", query);

        JSONObject aggs = new JSONObject();
        JSONObject distinct = new JSONObject();
        JSONObject terms = new JSONObject();
        terms.put("field","_index");
        terms.put("size","1000");
        distinct.put("terms",terms);
        aggs.put("terms_index",distinct);
        dsl.put("aggregations",aggs);

        List sortList = this.sort(sort);
        if (sortList != null && !sortList.isEmpty()) {
            dsl.put("sort", sortList);
        }
        LOGGER.info(dsl.toJSONString());
        Map result = elasticsearchRestClient.query(dsl.toJSONString(), indexName);
        if(result.get("aggs")!=null){
            Configuration aggsConfig = Configuration.from(result.get("aggs"));
            result.put("indexs",aggsConfig.get("terms_index.buckets"));
        }

        return this.setMetaList(result);
    }


    /**
     * 往数据结果里设置元数据信息
     *
     * @param result
     * @return
     */
    private Map setMetaList(Map result) {
        //result.put("meta",new HashMap<>());
        if (result.containsKey("data")) {
            Object data = result.get("data");
            if (data instanceof List) {
                ((List<Map>) data).forEach(map -> {
                    String index = (String) map.get("_index");
                    this.setMeta(result, index);
                });
            } else if (data instanceof Map) {
                String index = (String) ((Map) data).get("_index");
                this.setMeta(result, index);
            }
        }
        return result;
    }

    private Map setMeta(Map result, String indexName) {
        if (StringUtils.isNotBlank(indexName)) {
            EqaIndex list = eqaMetaMap.getEqaMetaList(indexName);
            if (list != null) {
                if (result.get("meta") == null) {
                    result.put("meta", new HashMap<>());
                }
                if (!((Map) result.get("meta")).containsKey(indexName)) {
                    ((Map) result.get("meta")).put(indexName, list.getEqaMetas());
                }
            }
        }
        return result;
    }

}
