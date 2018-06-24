package com.anluy.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.EqaConfig;
import com.anluy.admin.entity.TjfxCftJyds;
import com.anluy.admin.entity.TjfxCftJyls;
import com.anluy.admin.entity.TjfxJyls;
import com.anluy.admin.service.TjfxCftService;
import com.anluy.admin.utils.HTTPUtils;
import com.anluy.admin.utils.MD5;
import com.anluy.commons.dao.BaseDAO;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.service.BaseServiceImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 功能说明：财付通统计分析
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
@Service
public class TjfxCftServiceImpl extends BaseServiceImpl implements TjfxCftService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TjfxCftServiceImpl.class);
    private static final String CACHE_NAME = "Eqa-Aggs-Cache";
    @Resource
    private EqaConfig eqaConfig;

    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private CacheManager cacheManager;

    private String queryDsl;
    private String queryDslForIntegrated;

    public TjfxCftServiceImpl() {
        //dsl查询语句
        if (StringUtils.isBlank(queryDsl)) {
            try {
                queryDsl = IOUtils.toString(TjfxCftServiceImpl.class.getResourceAsStream("/dsl/eqa-queryCftJyls.json"));
            } catch (IOException e) {
                LOGGER.error("DSL文件加载错误", e);
            }
        }
        //dsl查询语句
        if (StringUtils.isBlank(queryDslForIntegrated)) {
            try {
                queryDslForIntegrated = IOUtils.toString(TjfxCftServiceImpl.class.getResourceAsStream("/dsl/eqa-queryCftData.json"));
            } catch (IOException e) {
                LOGGER.error("DSL文件加载错误", e);
            }
        }
    }

    /**
     * 整合数据
     *
     * @param cftId
     * @param token
     * @return
     * @throws Exception
     */
    @Override
    public Object integrated(String cftId, String token) throws Exception {
        String dsl = String.format(queryDslForIntegrated, cftId);
        elasticsearchRestClient.scroll(dsl, "1", new ElasticsearchRestClient.TimeWindowCallBack() {
            @Override
            public void process(List<Map> mapList) {
                List<Map> dataList = new ArrayList<>();
                if (!mapList.isEmpty()) {
                    boolean update = false;
                    for (Map map : mapList) {
                        update = false;
                        String zh = (String) map.get("zh");
                        String jdlx = (String) map.get("jdlx");
                        String jsf = (String) map.get("jsf");
                        String fsf = (String) map.get("fsf");
                        String ds_id = (String) map.get("ds_id");
                        Object jyjeO = map.get("jyje");
                        Double jyje = null;
                        if (jyjeO instanceof BigDecimal) {
                            jyje = ((BigDecimal) jyjeO).doubleValue();
                        } else if (jyjeO instanceof Double) {
                            jyje = ((Double) jyjeO);
                        } else {
                            jyje = Double.valueOf(jyjeO.toString());
                        }
                        Double zc100 = (Double) map.get("zc100");
                        if (StringUtils.isBlank(ds_id)) {
                            if (StringUtils.isBlank(fsf) && StringUtils.isBlank(jsf)) {
                                map.put("ds_id", "-");
                            } else if ("出".equals(jdlx)) {
                                if (StringUtils.isBlank(jsf))
                                    map.put("ds_id", "-");
                                else
                                    map.put("ds_id", jsf);
                            } else {
                                if (StringUtils.isBlank(fsf))
                                    map.put("ds_id", "-");
                                else
                                    map.put("ds_id", fsf);
                            }
                            update = true;
                        }
                        if (zc100 == null) {
                            update = true;
                            if (jyje != null) {
                                Double mod = jyje % 100;
                                map.put("zc100", mod);
                            } else {
                                map.put("zc100", -1);
                            }
                        }
                        if (update) {
                            dataList.add(map);
                        }
                        if (dataList.size() == 500) {
                            elasticsearchRestClient.batchUpdate(dataList, "cfttrades");
                            dataList.clear();
                        }
                    }
                    if (!dataList.isEmpty()) {
                        elasticsearchRestClient.batchUpdate(dataList, "cfttrades");
                    }
                }
            }
        }, "cfttrades", "id,zh,jdlx,jsf,fsf,ds_id,jyje,zc100", null);

        return null;
    }

    /**
     * 分析财付通交易流水数据
     * 并清洗流水的交易时间的 时、日、周、月等标签
     *
     * @param cftId 财付通id
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public Object analyzeJyls(String cftId, String jyjeRange, String dsId, String zcType, String token) throws IOException {
        String dsl = String.format(queryDsl, cftId);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);

        TjfxCftJyls tjfxCftJyls = new TjfxCftJyls();
        tjfxCftJyls.setCftId(cftId);
        this.aggJyls(dslJson, tjfxCftJyls, jyjeRange, dsId, zcType, token);
        return tjfxCftJyls;
    }

    /**
     * 分析财付通交易对手数据
     * 并清洗流水的交易时间的 时、日、周、月等标签
     *
     * @param cftId
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public Object analyzeJyds(String cftId, String zcType, String token) throws IOException {
        List<TjfxCftJyds> resultList = new ArrayList<>();
        String dsl = String.format(queryDsl, cftId);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);
        Set<String> jydsZh = new HashSet<>();

        JSONArray conditions = dslJson.getJSONArray("conditions");
        this.setConditionZcType100(conditions, zcType);

        JSONArray aggsJSONArray = dslJson.getJSONArray("aggs");
        aggsJSONArray.clear();
        //聚合对手账号
        JSONObject count = new JSONObject();
        count.put("groupName", "terms_ds");
        count.put("field", "ds_id");
        count.put("aggsType", 1);
        aggsJSONArray.add(count);
        JSONObject aggsObject = aggs(eqaConfig.getAggsUrl(), dslJson.toJSONString(), token);
        if (aggsObject != null) {
            JSONArray dsArray = aggsObject.getJSONArray("terms_ds");
            for (int i = 0; i < dsArray.size(); i++) {
                JSONObject cv = dsArray.getJSONObject(i);
                String dszh = cv.getString("key");
                jydsZh.add(dszh);
            }
        }

        JSONObject cond1 = new JSONObject();
        cond1.put("groupId", "group-field-1527089623689");
        cond1.put("groupType", "should");
        cond1.put("field", "ds_id");
        cond1.put("values", null);
        cond1.put("searchType", 1);
        cond1.put("dataType", 2);
        conditions.add(cond1);

        //统计对手
        jydsZh.forEach(dszh -> {
            cond1.put("values", new String[]{dszh});
            TjfxCftJyds jyds = new TjfxCftJyds();
            jyds.setDfId(dszh);
            this.aggJyls(dslJson, jyds, null, null, zcType, token);
            resultList.add(jyds);
        });
        Collections.sort(resultList, new Comparator<TjfxCftJyds>() {
            @Override
            public int compare(TjfxCftJyds o1, TjfxCftJyds o2) {
                if (o1.getLjjyje() > o2.getLjjyje()) {
                    return -1;
                } else if (o1.getLjjyje() < o2.getLjjyje()) {
                    return 1;
                }
                return 0;
            }
        });
        return resultList;
    }


    /**
     * 统计交易金额区间
     *
     * @param cftId
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public Object analyzeJyje(String cftId, String dsId, String zcType, String token) throws IOException {
        String dsl = String.format(queryDsl, cftId);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);
        JSONArray aggsJSONArray = dslJson.getJSONArray("aggs");
        aggsJSONArray.clear();

        JSONArray conditions = dslJson.getJSONArray("conditions");
        this.setConditionDsId(conditions, dsId);
        this.setConditionZcType100(conditions, zcType);
        List<Double[]> rangeList = new ArrayList<>();
        rangeList.add(new Double[]{0.00, 10.00});
        rangeList.add(new Double[]{10.00, 50.00});
        rangeList.add(new Double[]{50.00, 100.00});
        rangeList.add(new Double[]{100.00, 500.00});
        rangeList.add(new Double[]{500.00, 1000.00});
        rangeList.add(new Double[]{1000.00, 5000.00});
        rangeList.add(new Double[]{5000.00, 10000.00});
        rangeList.add(new Double[]{10000.00, 50000.00});
        rangeList.add(new Double[]{50000.00, null});
        List<Map> values = new ArrayList<>();
        for (Double[] range : rangeList) {
            Map<String, Double> map = new HashMap<>();
            map.put("from", range[0]);
            map.put("to", range[1]);
            values.add(map);
        }
        //
        JSONObject max = new JSONObject();
        max.put("groupName", "group_jyje");
        max.put("field", "jyje");
        max.put("values", values);
        max.put("aggsType", 8);
        aggsJSONArray.add(max);
        JSONObject aggsObj = aggs(eqaConfig.getAggsUrl(), JSON.toJSONString(dslJson), token);
        return aggsObj;
    }


    /**
     * 统计交易金额区间
     *
     * @param cftId
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public Object analyzeZc100(String cftId, String dsId, String token) throws IOException {
        String dsl = String.format(queryDsl, cftId);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);
        JSONArray aggsJSONArray = dslJson.getJSONArray("aggs");
        aggsJSONArray.clear();

        JSONArray conditions = dslJson.getJSONArray("conditions");
        this.setConditionDsId(conditions, dsId);

        JSONObject cond = new JSONObject();
        cond.put("field", "zc100");
        cond.put("values", new String[]{"0"});
        cond.put("searchType", 1);
        cond.put("dataType", 3);
        conditions.add(cond);

        JSONObject max = new JSONObject();
        max.put("groupName", "group_zc0");
        max.put("field", "zc100");
        max.put("aggsType", 7);
        aggsJSONArray.add(max);
        JSONObject aggsObj = aggs(eqaConfig.getAggsUrl(), JSON.toJSONString(dslJson), token);

        cond.put("searchType", 3);
        JSONObject aggsObj2 = aggs(eqaConfig.getAggsUrl(), JSON.toJSONString(dslJson), token);
        Map result = new HashMap();
        result.put("zc100", aggsObj);
        result.put("nzc100", aggsObj2);
        return result;
    }

    /**
     * 统计交易流水
     *
     * @param dslJson
     * @param jyls
     * @param jyjeRange
     * @param dsId
     * @param token
     * @return
     */
    private TjfxJyls aggJyls(JSONObject dslJson, TjfxJyls jyls, String jyjeRange, String dsId, String zcType, String token) {
        JSONArray conditions = dslJson.getJSONArray("conditions");
        if (StringUtils.isNotBlank(jyjeRange)) {
            JSONObject cond = new JSONObject();
            cond.put("field", "jyje");
            cond.put("values", jyjeRange.replace("*", "").split("-"));
            cond.put("searchType", 6);
            cond.put("dataType", 3);
            conditions.add(cond);
        }
        this.setConditionDsId(conditions, dsId);
        this.setConditionZcType100(conditions, zcType);

        JSONObject aggsObj = aggsJyje(dslJson, token);
        if (aggsObj != null) {
            jyls.setZdjyje(aggsObj.getDouble("max_jyje"));
            jyls.setZxjyje(aggsObj.getDouble("min_jyje"));
            jyls.setPjjyje(aggsObj.getDouble("avg_jyje"));
            jyls.setLjjyje(aggsObj.getDouble("sum_jyje"));
            jyls.setLjjybs(aggsObj.getInteger("count_jyje"));
        }
        JSONObject cond3 = new JSONObject();
        cond3.put("field", "jdlx");
        cond3.put("values", new String[]{"出"});
        cond3.put("searchType", 1);
        cond3.put("dataType", 2);
        conditions.add(cond3);
        aggsObj = aggsJyje(dslJson, token);
        if (aggsObj != null) {
            jyls.setZdzcje(aggsObj.getDouble("max_jyje"));
            jyls.setZxzcje(aggsObj.getDouble("min_jyje"));
            jyls.setLjzcje(aggsObj.getDouble("sum_jyje"));
            jyls.setLjzcbs(aggsObj.getInteger("count_jyje"));
        }
        cond3.put("values", new String[]{"入"});
        aggsObj = aggsJyje(dslJson, token);
        if (aggsObj != null) {
            jyls.setZdzrje(aggsObj.getDouble("max_jyje"));
            jyls.setZxzrje(aggsObj.getDouble("min_jyje"));
            jyls.setLjzrje(aggsObj.getDouble("sum_jyje"));
            jyls.setLjzrbs(aggsObj.getInteger("count_jyje"));
        }
        conditions.remove(conditions.size() - 1);
        aggsObj = aggsDate(dslJson, token);
        if (aggsObj != null) {
            jyls.setZzjysj(aggsObj.getDate("min_jysj"));
            jyls.setZwjysj(aggsObj.getDate("max_jysj"));
        }
        return jyls;
    }

    /**
     * 统计金额信息
     *
     * @param dslJson
     * @param token
     * @return
     */
    private JSONObject aggsJyje(JSONObject dslJson, String token) {
        JSONArray aggsJSONArray = dslJson.getJSONArray("aggs");
        aggsJSONArray.clear();
        //最大值
        JSONObject max = new JSONObject();
        max.put("groupName", "max_jyje");
        max.put("field", "jyje");
        max.put("aggsType", 2);
        aggsJSONArray.add(max);
        //最小值
        JSONObject min = new JSONObject();
        min.put("groupName", "min_jyje");
        min.put("field", "jyje");
        min.put("aggsType", 3);
        aggsJSONArray.add(min);
        //平均值
        JSONObject avg = new JSONObject();
        avg.put("groupName", "avg_jyje");
        avg.put("field", "jyje");
        avg.put("aggsType", 4);
        aggsJSONArray.add(avg);
        //求和
        JSONObject sum = new JSONObject();
        sum.put("groupName", "sum_jyje");
        sum.put("field", "jyje");
        sum.put("aggsType", 5);
        aggsJSONArray.add(sum);
        //计数
        JSONObject count = new JSONObject();
        count.put("groupName", "count_jyje");
        count.put("field", "jyje");
        count.put("aggsType", 7);
        aggsJSONArray.add(count);

        String dsl = dslJson.toJSONString();
        EhCacheCache cache = (EhCacheCache) cacheManager.getCache(CACHE_NAME);
        String key = MD5.encode(dsl);
        Object cacheObj = cache.get(key);
        JSONObject result = null;
        if (cacheObj != null) {
            result = (JSONObject) ((SimpleValueWrapper) cacheObj).get();
        } else {
            result = aggs(eqaConfig.getAggsUrl(), dsl, token);
            if (result != null && !result.isEmpty() && StringUtils.isNotBlank(result.getString("max_jyje"))) {
                cache.put(key, result);
            }
        }
        return result;
    }

    /**
     * 统计时间
     *
     * @param dslJson
     * @param token
     * @return
     */
    private JSONObject aggsDate(JSONObject dslJson, String token) {
        //最早最晚交易时间
        JSONArray aggsJSONArray = dslJson.getJSONArray("aggs");
        aggsJSONArray.clear();
        //最大值
        JSONObject max = new JSONObject();
        max.put("groupName", "max_jysj");
        max.put("field", "jysj");
        max.put("aggsType", 2);
        aggsJSONArray.add(max);
        //最小值
        JSONObject min = new JSONObject();
        min.put("groupName", "min_jysj");
        min.put("field", "jysj");
        min.put("aggsType", 3);
        aggsJSONArray.add(min);

        String dsl = dslJson.toJSONString();
        EhCacheCache cache = (EhCacheCache) cacheManager.getCache(CACHE_NAME);
        String key = MD5.encode(dsl);
        Object cacheObj = cache.get(key);
        JSONObject result = null;
        if (cacheObj != null) {
            result = (JSONObject) ((SimpleValueWrapper) cacheObj).get();
        } else {
            result = aggs(eqaConfig.getAggsUrl(), dsl, token);
            if (result != null && !result.isEmpty() && StringUtils.isNotBlank(result.getString("max_jysj"))) {
                cache.put(key, result);
            }
        }
        return result;
    }

    /**
     * 设置对手id条件
     *
     * @param conditions
     * @param dsId
     */
    private void setConditionDsId(JSONArray conditions, String dsId) {
        if (StringUtils.isNotBlank(dsId)) {
            JSONObject cond = new JSONObject();
            cond.put("field", "ds_id");
            cond.put("values", new String[]{dsId});
            cond.put("searchType", 1);
            cond.put("dataType", 2);
            conditions.add(cond);
        }
    }

    /**
     * 设置被100整除条件
     *
     * @param conditions
     * @param zcType
     */
    private void setConditionZcType100(JSONArray conditions, String zcType) {
        if (StringUtils.isNotBlank(zcType)) {
            JSONObject cond = new JSONObject();
            cond.put("field", "zc100");
            cond.put("values", new String[]{"0"});
            cond.put("dataType", 2);
            conditions.add(cond);
            if ("100".equals(zcType)) {
                cond.put("searchType", 1);
            } else if ("-100".equals(zcType)) {
                cond.put("searchType", 3);
            }
        }
    }

    @Override
    public BaseDAO getBaseDAO() {
        return null;
    }

    @Override
    public ElasticsearchRestClient getElasticsearchRestClient() {
        return null;
    }

    @Override
    public String getIndexName() {
        return null;
    }
}
