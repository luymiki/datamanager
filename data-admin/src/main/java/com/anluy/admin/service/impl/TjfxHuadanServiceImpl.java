package com.anluy.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.EqaConfig;
import com.anluy.admin.entity.TjfxHuadan;
import com.anluy.admin.entity.TjfxHuadanDs;
import com.anluy.admin.entity.TjfxHuadanLs;
import com.anluy.admin.service.TjfxHuadanService;
import com.anluy.commons.dao.BaseDAO;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.service.BaseServiceImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * 功能说明：统计话单分析
 * <p>
 * Created by hc.zeng on 2018/5/26.
 */
@Service
public class TjfxHuadanServiceImpl extends BaseServiceImpl implements TjfxHuadanService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TjfxHuadanServiceImpl.class);

    @Resource
    private EqaConfig eqaConfig;

    private String queryDsl;

    public TjfxHuadanServiceImpl() {
        //dsl查询语句
        if (StringUtils.isBlank(queryDsl)) {
            try {
                queryDsl = IOUtils.toString(TjfxCftServiceImpl.class.getResourceAsStream("/dsl/eqa-queryHuadan.json"));
            } catch (IOException e) {
                LOGGER.error("DSL文件加载错误", e);
            }
        }
    }

    /**
     * 分析通话记录流水的信息
     *
     * @param hdid
     * @param token
     * @return
     */
    @Override
    public Object analyzeThjl(String hdid, String token) {

        String dsl = String.format(queryDsl, hdid);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);

        TjfxHuadanLs tjfxCftJyls = new TjfxHuadanLs();
        this.aggThls(dslJson, tjfxCftJyls, token);
        Set<String> zhset = this.aggDdhm(dslJson, token);
        tjfxCftJyls.setDdsl(zhset.size());
        //其他次数
        Integer zcs = tjfxCftJyls.getZthcs();
        Integer zjcs = tjfxCftJyls.getZjcs();
        Integer bjcs = tjfxCftJyls.getBjcs();
        if (zcs == null) {
            tjfxCftJyls.setQtcs(0);
        } else {
            zjcs = zjcs == null ? 0 : zjcs;
            bjcs = bjcs == null ? 0 : bjcs;
            tjfxCftJyls.setQtcs(zcs - zjcs - bjcs);
        }

        return tjfxCftJyls;
    }

    /**
     * 分析通话对端记录流水的信息
     *
     * @param hdid
     * @param token
     * @return
     */
    @Override
    public Object analyzeDdxx(String hdid, String token) {
        List<TjfxHuadanDs> resultList = new ArrayList<>();
        String dsl = String.format(queryDsl, hdid);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);
        Set<String> jydsZh = new HashSet<>();

        JSONArray aggsJSONArray = dslJson.getJSONArray("aggs");
        aggsJSONArray.clear();
        //聚合对端账号
        JSONObject count = new JSONObject();
        count.put("groupName", "terms_ds");
        count.put("field", "ddhm");
        count.put("aggsType", 1);
        aggsJSONArray.add(count);
        JSONObject aggsObject = aggs(eqaConfig.getAggsUrl(), dslJson.toJSONString(), token);
        if (aggsObject != null) {
            JSONArray dsArray = aggsObject.getJSONArray("terms_ds");
            for (int i = 0; i < dsArray.size(); i++) {
                JSONObject cv = dsArray.getJSONObject(i);
                String dszh = cv.getString("key");
                if (!jydsZh.contains(dszh)) {
                    jydsZh.add(dszh);
                }
            }
        }
        JSONArray conditions = dslJson.getJSONArray("conditions");
        JSONObject cond1 = new JSONObject();
        cond1.put("field", "ddhm");
        cond1.put("values", null);
        cond1.put("searchType", 1);
        cond1.put("dataType", 2);
        conditions.add(cond1);

        //统计对手
        jydsZh.forEach(dszh -> {
            cond1.put("values", new String[]{dszh});
            TjfxHuadanDs jyds = new TjfxHuadanDs();
            jyds.setDdhm(dszh);
            this.aggThls(dslJson, jyds, token);
            JSONObject aggsObj = aggsDate(dslJson, token);
            if (aggsObj != null) {
                jyds.setZzthsj(aggsObj.getDate("min_kssj"));
                jyds.setZwthsj(aggsObj.getDate("max_kssj"));
            }

            resultList.add(jyds);
        });
        return resultList;
    }

    /**
     * 统计交易金额区间
     *
     * @param hdId
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public Object analyzeThsc(String hdId, String token) {
        String dsl = String.format(queryDsl, hdId);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);
        JSONArray aggsJSONArray = dslJson.getJSONArray("aggs");
        aggsJSONArray.clear();


        List<Integer[]> rangeList = new ArrayList<>();
        rangeList.add(new Integer[]{0, 10});
        rangeList.add(new Integer[]{10, 50});
        rangeList.add(new Integer[]{50, 100});
        rangeList.add(new Integer[]{100, 500});
        rangeList.add(new Integer[]{500, 1000});
        rangeList.add(new Integer[]{1000, 5000});
        rangeList.add(new Integer[]{5000, null});
        List<Map> values = new ArrayList<>();
        for (Integer[] range : rangeList) {
            Map<String, Integer> map = new HashMap<>();
            map.put("from", range[0]);
            map.put("to", range[1]);
            values.add(map);
        }
        //
        JSONObject max = new JSONObject();
        max.put("groupName", "group_thsc");
        max.put("field", "thsc");
        max.put("values", values);
        max.put("aggsType", 8);
        aggsJSONArray.add(max);
        JSONObject aggsObj = aggs(eqaConfig.getAggsUrl(), JSON.toJSONString(dslJson), token);
        return aggsObj;
    }

    /**
     * 统计通话流水信息
     *
     * @param dslJson
     * @param thls
     * @param token
     * @return
     */
    private TjfxHuadan aggThls(JSONObject dslJson, TjfxHuadan thls, String token) {
        JSONArray conditions = dslJson.getJSONArray("conditions");
        JSONObject aggsObj = aggsThsc(dslJson, token);
        if (aggsObj != null) {
            thls.setZthcs(aggsObj.getInteger("count_thsc"));
            thls.setZthsc(aggsObj.getInteger("sum_thsc"));
            thls.setZcthsc(aggsObj.getInteger("max_thsc"));
            thls.setZdthsc(aggsObj.getInteger("min_thsc"));
        }
        JSONObject cond3 = new JSONObject();
        cond3.put("field", "hjlx");
        cond3.put("values", new String[]{"主叫"});
        cond3.put("searchType", 1);
        cond3.put("dataType", 2);
        conditions.add(cond3);
        aggsObj = aggsThsc(dslJson, token);
        if (aggsObj != null) {
            thls.setZjcs(aggsObj.getInteger("count_thsc"));
            thls.setZjthsc(aggsObj.getInteger("sum_thsc"));
            thls.setZjzcthsc(aggsObj.getInteger("max_thsc"));
            thls.setZjzdthsc(aggsObj.getInteger("min_thsc"));
        }
        cond3.put("values", new String[]{"被叫"});
        aggsObj = aggsThsc(dslJson, token);
        if (aggsObj != null) {
            thls.setBjcs(aggsObj.getInteger("count_thsc"));
            thls.setBjthsc(aggsObj.getInteger("sum_thsc"));
            thls.setBjzcthsc(aggsObj.getInteger("max_thsc"));
            thls.setBjzdthsc(aggsObj.getInteger("min_thsc"));
        }
        return thls;
    }

    private Set<String> aggDdhm(JSONObject dslJson, String token) {
        Set<String> jydsZh = new HashSet<>();
        JSONArray aggsJSONArray = dslJson.getJSONArray("aggs");
        aggsJSONArray.clear();
        //聚合对端账号
        JSONObject count = new JSONObject();
        count.put("groupName", "terms_ds");
        count.put("field", "ddhm");
        count.put("aggsType", 1);
        aggsJSONArray.add(count);
        JSONObject aggsObject = aggs(eqaConfig.getAggsUrl(), dslJson.toJSONString(), token);
        if (aggsObject != null) {
            JSONArray dsArray = aggsObject.getJSONArray("terms_ds");
            for (int i = 0; i < dsArray.size(); i++) {
                JSONObject cv = dsArray.getJSONObject(i);
                String dszh = cv.getString("key");
                if (!jydsZh.contains(dszh)) {
                    jydsZh.add(dszh);
                }
            }
        }
        return jydsZh;
    }


    /**
     * 统计时长信息
     *
     * @param dslJson
     * @param token
     * @return
     */
    private JSONObject aggsThsc(JSONObject dslJson, String token) {
        JSONArray aggsJSONArray = dslJson.getJSONArray("aggs");
        aggsJSONArray.clear();
        //最大值
        JSONObject max = new JSONObject();
        max.put("groupName", "max_thsc");
        max.put("field", "thsc");
        max.put("aggsType", 2);
        aggsJSONArray.add(max);
        //最小值
        JSONObject min = new JSONObject();
        min.put("groupName", "min_thsc");
        min.put("field", "thsc");
        min.put("aggsType", 3);
        aggsJSONArray.add(min);

        //求和
        JSONObject sum = new JSONObject();
        sum.put("groupName", "sum_thsc");
        sum.put("field", "thsc");
        sum.put("aggsType", 5);
        aggsJSONArray.add(sum);
        //计数
        JSONObject count = new JSONObject();
        count.put("groupName", "count_thsc");
        count.put("field", "thsc");
        count.put("aggsType", 7);
        aggsJSONArray.add(count);
        return aggs(eqaConfig.getAggsUrl(), dslJson.toJSONString(), token);
    }

    /**
     * 统计时间
     *
     * @param dslJson
     * @param token
     * @return
     */
    private JSONObject aggsDate(JSONObject dslJson, String token) {
        //最早最晚通话时间
        JSONArray aggsJSONArray = dslJson.getJSONArray("aggs");
        aggsJSONArray.clear();
        //最大值
        JSONObject max = new JSONObject();
        max.put("groupName", "max_kssj");
        max.put("field", "kssj");
        max.put("aggsType", 2);
        aggsJSONArray.add(max);
        //最小值
        JSONObject min = new JSONObject();
        min.put("groupName", "min_kssj");
        min.put("field", "kssj");
        min.put("aggsType", 3);
        aggsJSONArray.add(min);
        return aggs(eqaConfig.getAggsUrl(), dslJson.toJSONString(), token);
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
