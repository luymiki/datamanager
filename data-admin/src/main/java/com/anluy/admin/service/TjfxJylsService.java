package com.anluy.admin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.entity.TjfxJyds;
import com.anluy.admin.entity.TjfxJyls;
import com.anluy.admin.utils.ExcelExportUtil;
import com.anluy.admin.utils.MD5;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.support.SimpleValueWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/6/25.
 */
public interface TjfxJylsService extends TjfxService {
    String CACHE_NAME = "Eqa-Aggs-Cache";

    String getAggUrl();

    CacheManager getCcacheManager();

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
    default TjfxJyls aggJyls(JSONObject dslJson, TjfxJyls jyls, String jyjeRange, String dsId, String zcType, String token) {
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
        cond3.put("values", new String[]{"入", "进"});
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
    default JSONObject aggsJyje(JSONObject dslJson, String token) {
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
        EhCacheCache cache = (EhCacheCache) this.getCcacheManager().getCache(CACHE_NAME);
        String key = MD5.encode(dsl);
        Object cacheObj = cache.get(key);
        JSONObject result = null;
        if (cacheObj != null) {
            result = (JSONObject) ((SimpleValueWrapper) cacheObj).get();
        } else {
            result = this.aggs(this.getAggUrl(), dsl, token);
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
    default JSONObject aggsDate(JSONObject dslJson, String token) {
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
        EhCacheCache cache = (EhCacheCache) this.getCcacheManager().getCache(CACHE_NAME);
        String key = MD5.encode(dsl);
        Object cacheObj = cache.get(key);
        JSONObject result = null;
        if (cacheObj != null) {
            result = (JSONObject) ((SimpleValueWrapper) cacheObj).get();
        } else {
            result = this.aggs(this.getAggUrl(), dsl, token);
            if (result != null && !result.isEmpty() && StringUtils.isNotBlank(result.getString("max_jysj"))) {
                cache.put(key, result);
            }
        }
        return result;
    }

    /**
     * 统计交易流水信息
     *
     * @param dsId
     * @param zcType
     * @param token
     * @return
     * @throws IOException
     */
    default Object analyzeJyje(JSONObject dslJson, String dsId, String zcType, String token) throws IOException {
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
        JSONObject aggsObj = this.aggs(this.getAggUrl(), JSON.toJSONString(dslJson), token);
        return aggsObj;
    }

    /**
     * 分析财付通交易对手数据
     * 并清洗流水的交易时间的 时、日、周、月等标签
     *
     * @param token
     * @return
     * @throws IOException
     */
    default Object analyzeJyds(JSONObject dslJson, TjfxJyds tjfxJyds, String zcType, String token) throws IOException {
        List<TjfxJyds> resultList = new ArrayList<>();
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
        JSONObject aggsObject = this.aggs(this.getAggUrl(), dslJson.toJSONString(), token);
        if (aggsObject != null) {
            JSONArray dsArray = aggsObject.getJSONArray("terms_ds");
            for (int i = 0; i < dsArray.size(); i++) {
                JSONObject cv = dsArray.getJSONObject(i);
                String dszh = cv.getString("key");
                jydsZh.add(dszh);
            }
        }

        JSONObject cond1 = new JSONObject();
//        cond1.put("groupId", "group-field-1527089623689");
//        cond1.put("groupType", "should");
        cond1.put("field", "ds_id");
        cond1.put("values", null);
        cond1.put("searchType", 1);
        cond1.put("dataType", 2);
        conditions.add(cond1);

        JSON jo = (JSON) JSON.toJSON(tjfxJyds);
        //统计对手
        jydsZh.forEach(dszh -> {
            cond1.put("values", new String[]{dszh});
            TjfxJyds jyds = JSON.toJavaObject(jo, tjfxJyds.getClass());
            //TjfxJyds jyds = ObjectUtils.clone(tjfxJyds);
            jyds.setDfId(dszh);
            this.aggJyls(dslJson, jyds, null, null, zcType, token);
            resultList.add(jyds);
        });
        Collections.sort(resultList, new Comparator<TjfxJyds>() {
            @Override
            public int compare(TjfxJyds o1, TjfxJyds o2) {
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
     * 统计被100整除的数据
     *
     * @param aggurl
     * @param dslJson
     * @param dsId
     * @param token
     * @return
     */
    default Object analyzeZc100(String aggurl, JSONObject dslJson, String dsId, String token) {
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
        JSONObject aggsObj = aggs(aggurl, JSON.toJSONString(dslJson), token);

        cond.put("searchType", 3);
        JSONObject aggsObj2 = aggs(aggurl, JSON.toJSONString(dslJson), token);
        JSONObject result = new JSONObject();
        result.put("zc100", aggsObj);
        result.put("nzc100", aggsObj2);
        return result;
    }

    /**
     * 设置对手id条件
     *
     * @param conditions
     * @param dsId
     */
    default void setConditionDsId(JSONArray conditions, String dsId) {
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
    default void setConditionZcType100(JSONArray conditions, String zcType) {
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

    default void exportExcel(HttpServletRequest request, HttpServletResponse response, JSONObject dataMap, String fileNameCn) throws Exception {
        //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("multipart/form-data");
        //2.设置文件头：最后一个参数是设置下载文件名(假如我们叫a.pdf)
        String fileName = "数据导出-" + fileNameCn + ".xls";
        fileName = new String(fileName.getBytes(), "ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> beans = new HashMap<>();
        beans.put("title", fileNameCn);
        EhCacheCache cache = (EhCacheCache) this.getCcacheManager().getCache(CACHE_NAME);
        Object cacheObj = cache.get(dataMap.get("hzid"));
        if (cacheObj != null) {
            JSONObject r = (JSONObject) ((SimpleValueWrapper) cacheObj).get();
            List l = new ArrayList<>();
            r.put("xh", 1);
            l.add(r);
            beans.put("dataList", l);
        }
        Object cacheObj2 = cache.get(dataMap.get("qjfbid"));
        if (cacheObj2 != null) {
            JSONObject r = (JSONObject) ((SimpleValueWrapper) cacheObj2).get();
            JSONArray jsonArray = r.getJSONArray("group_jyje");
            JSONObject jo = new JSONObject();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject oo = (JSONObject) jsonArray.get(i);
                jo.put("qjfb"+i, oo.getIntValue("doc_count"));
            }
            beans.put("qjfb", jo);
        }
        Object cacheObj3 = cache.get(dataMap.get("jefbid"));
        if (cacheObj3 != null) {
            JSONObject r = (JSONObject) ((SimpleValueWrapper) cacheObj3).get();
            JSONObject jo = new JSONObject();
            jo.put("Y100", r.getJSONObject("zc100").getInteger("group_zc0"));
            jo.put("N100", r.getJSONObject("nzc100").getInteger("group_zc0"));
            beans.put("jefb", jo);
        }
        Object cacheObj4 = cache.get(dataMap.get("dshzid"));
        if (cacheObj4 != null) {
            JSONArray r = (JSONArray) ((SimpleValueWrapper) cacheObj4).get();
            for (int i = 0; i < r.size(); i++) {
                r.getJSONObject(i).put("xh", i + 1);
            }
            beans.put("dsDataList", r);
        }
        ExcelExportUtil.exportExcel( "/template/export2.xls",beans,response.getOutputStream());
    }
}
