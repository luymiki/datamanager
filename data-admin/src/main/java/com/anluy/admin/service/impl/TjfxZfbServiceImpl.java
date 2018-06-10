package com.anluy.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.EqaConfig;
import com.anluy.admin.entity.*;
import com.anluy.admin.service.TjfxCftService;
import com.anluy.admin.service.TjfxZfbService;
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
import java.math.BigDecimal;
import java.util.*;

/**
 * 功能说明：支付宝统计分析
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
@Service
public class TjfxZfbServiceImpl extends BaseServiceImpl implements TjfxZfbService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TjfxZfbServiceImpl.class);

    @Resource
    private EqaConfig eqaConfig;

    private String queryDsl;
    private String queryDslForIntegrated;

    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    public TjfxZfbServiceImpl() {
        //dsl查询语句
        if (StringUtils.isBlank(queryDsl)) {
            try {
                queryDsl = IOUtils.toString(TjfxZfbServiceImpl.class.getResourceAsStream("/dsl/eqa-queryZfbJyls.json"));
            } catch (IOException e) {
                LOGGER.error("DSL文件加载错误", e);
            }
        }
        //dsl查询语句
        if (StringUtils.isBlank(queryDslForIntegrated)) {
            try {
                queryDslForIntegrated = IOUtils.toString(TjfxZfbServiceImpl.class.getResourceAsStream("/dsl/eqa-queryZfbdata.json"));
            } catch (IOException e) {
                LOGGER.error("DSL文件加载错误", e);
            }
        }
    }

    /**
     * 整合清洗数据
     *
     * @param userId
     * @param xcbh
     * @param token
     * @return
     * @throws Exception
     */
    @Override
    public Object integrated(String userId, String xcbh, String token) throws Exception {
        //zfbjyjlinfo,zfbtxinfo,zfbzhinfo,zfbzzinfo
        //zfbzhinfo 账户明细表
        String dsl = String.format(queryDslForIntegrated, userId, xcbh);
        elasticsearchRestClient.scroll(dsl, "1", new ElasticsearchRestClient.TimeWindowCallBack() {
            @Override
            public void process(List<Map> mapList) {
                List<Map> dataList = new ArrayList<>();
                if (!mapList.isEmpty()) {
                    boolean update = false;
                    for (Map map : mapList) {
                        update = false;
                        String sjbj = (String) map.get("sjbj");
                        String jdlx = (String) map.get("jdlx");
                        String jycjsj = (String) map.get("jycjsj");
                        String jysj = (String) map.get("jysj");
                        Object je = map.get("je");
                        Object jyje =map.get("jyje");
                        String df_user_id = (String) map.get("df_user_id");
                        String ds_id = (String) map.get("ds_id");
                        if(StringUtils.isNotBlank(sjbj)){
                            if(StringUtils.isBlank(jdlx)){
                                update = true;
                                if("支出".equals(sjbj))
                                    map.put("jdlx","出");
                                else
                                    map.put("jdlx","入");
                            }
                        }
                        if(StringUtils.isNotBlank(jycjsj)){
                            if(StringUtils.isBlank(jysj)){
                                update = true;
                                map.put("jysj",jycjsj);
                            }
                        }
                        if(StringUtils.isNotBlank(df_user_id)){
                            if(StringUtils.isBlank(ds_id)){
                                update = true;
                                map.put("ds_id",df_user_id);
                            }
                        }
                        if(je != null){
                            if(jyje == null){
                                update = true;
                                map.put("jyje",je);
                            }
                        }
                        if(update){
                            map.put("_id",map.get("id"));
                            dataList.add(map);
                        }
                        if(dataList.size() == 500){
                            elasticsearchRestClient.batchUpdate(dataList,"zfbzhinfo");
                            dataList.clear();
                        }
                    }
                    if( !dataList.isEmpty()){
                        elasticsearchRestClient.batchUpdate(dataList,"zfbzhinfo");
                    }
                }
            }
        }, "zfbzhinfo", "id,sjbj,jdlx,jycjsj,jysj,je,jyje,df_user_id,ds_id", null);

        // 提现记录全部都为转出
        elasticsearchRestClient.scroll(dsl, "1", new ElasticsearchRestClient.TimeWindowCallBack() {
            @Override
            public void process(List<Map> mapList) {
                List<Map> dataList = new ArrayList<>();
                if (!mapList.isEmpty()) {
                    boolean update = false;
                    for (Map map : mapList) {
                        update = false;
                        String jdlx = (String) map.get("jdlx");
                        String sqsj = (String) map.get("sqsj");
                        String jysj = (String) map.get("jysj");
                        Object je =  map.get("je");
                        Object jyje =  map.get("jyje");
                        String yhzh = (String) map.get("yhzh");
                        String ds_id = (String) map.get("ds_id");
                        if(StringUtils.isBlank(jdlx)){
                            update = true;
                            map.put("_id",map.get("id"));
                            map.put("jdlx","出");
                        }
                        if(StringUtils.isNotBlank(sqsj)){
                            if(StringUtils.isBlank(jysj)){
                                update = true;
                                map.put("jysj",sqsj);
                            }
                        }
                        if(je != null){
                            if(jyje == null){
                                update = true;
                                map.put("jyje",je);
                            }
                        }
                        if(StringUtils.isNotBlank(yhzh)){
                            if(StringUtils.isBlank(ds_id)){
                                update = true;
                                map.put("ds_id",yhzh);
                            }
                        }
                        if(update){
                            map.put("_id",map.get("id"));
                            dataList.add(map);
                        }
                        if(dataList.size() == 500){
                            elasticsearchRestClient.batchUpdate(dataList,"zfbtxinfo");
                            dataList.clear();
                        }
                    }
                    if( !dataList.isEmpty()){
                        elasticsearchRestClient.batchUpdate(dataList,"zfbtxinfo");
                    }
                }
            }
        }, "zfbtxinfo", "id,jdlx,sqsj,jysj,je,jyje,yhzh,ds_id", null);

        //转账记录如果付款方为支付宝账号，则这条记录为转出
        elasticsearchRestClient.scroll(dsl, "1", new ElasticsearchRestClient.TimeWindowCallBack() {
            @Override
            public void process(List<Map> mapList) {
                List<Map> dataList = new ArrayList<>();
                if (!mapList.isEmpty()) {
                    boolean update = false;
                    for (Map map : mapList) {
                        update = false;
                        String fkf_id = (String) map.get("fkf_id");
                        String jdlx = (String) map.get("jdlx");
                        String dzsj = (String) map.get("dzsj");
                        String jysj = (String) map.get("jysj");
                        Object je =  map.get("je");
                        Object jyje = map.get("jyje");
                        String skf_id = (String) map.get("skf_id");
                        String ds_id = (String) map.get("ds_id");
                        if(StringUtils.isBlank(jdlx)){
                            update = true;
                            map.put("_id",map.get("id"));
                            if(fkf_id.equals(userId))//如果是付款方账户id等于支付宝id说明是转出
                                map.put("jdlx","出");
                            else
                                map.put("jdlx","入");
                        }
                        if(StringUtils.isNotBlank(dzsj)){
                            if(StringUtils.isBlank(jysj)){
                                update = true;
                                map.put("jysj",dzsj);
                            }
                        }
                        if(je != null){
                            if(jyje == null){
                                update = true;
                                map.put("jyje",je);
                            }
                        }
                        if(StringUtils.isBlank(ds_id)){
                            update = true;
                            if(userId.equals(fkf_id))
                                map.put("ds_id",skf_id);
                            if(userId.equals(skf_id))
                                map.put("ds_id",fkf_id);
                        }
                        if(update){
                            map.put("_id",map.get("id"));
                            dataList.add(map);
                        }
                        if(dataList.size() == 500){
                            elasticsearchRestClient.batchUpdate(dataList,"zfbzzinfo");
                            dataList.clear();
                        }
                    }
                    if( !dataList.isEmpty()){
                        elasticsearchRestClient.batchUpdate(dataList,"zfbzzinfo");
                    }
                }
            }
        }, "zfbzzinfo", "id,fkf_id,jdlx,dzsj,jysj,je,jyje,skf_id,ds_id", null);

        //交易记录为淘宝购买记录，所以全部都为转出
        elasticsearchRestClient.scroll(dsl, "1", new ElasticsearchRestClient.TimeWindowCallBack() {
            @Override
            public void process(List<Map> mapList) {
                List<Map> dataList = new ArrayList<>();
                if (!mapList.isEmpty()) {
                    boolean update = false;
                    for (Map map : mapList) {
                        update = false;
                        String jdlx = (String) map.get("jdlx");
                        String cjsj = (String) map.get("cjsj");
                        String jysj = (String) map.get("jysj");
                        Object je = map.get("je");
                        Object jyje =  map.get("jyje");
                        String mj_id = (String) map.get("mj_id");
                        String ds_id = (String) map.get("ds_id");
                        if(StringUtils.isBlank(jdlx)){
                            update = true;
                            map.put("_id",map.get("id"));
                            map.put("jdlx","出");
                        }
                        if(StringUtils.isNotBlank(cjsj)){
                            if(StringUtils.isBlank(jysj)){
                                update = true;
                                map.put("jysj",cjsj);
                            }
                        }
                        if(je != null){
                            if(jyje == null){
                                update = true;
                                map.put("jyje",je);
                            }
                        }
                        if(StringUtils.isNotBlank(mj_id)){
                            if(StringUtils.isBlank(ds_id)){
                                update = true;
                                map.put("ds_id",mj_id);
                            }
                        }
                        if(update){
                            map.put("_id",map.get("id"));
                            dataList.add(map);
                        }
                        if(dataList.size() == 500){
                            elasticsearchRestClient.batchUpdate(dataList,"zfbjyjlinfo");
                            dataList.clear();
                        }
                    }
                    if( !dataList.isEmpty()){
                        elasticsearchRestClient.batchUpdate(dataList,"zfbjyjlinfo");
                    }
                }
            }
        }, "zfbjyjlinfo", "id,jdlx,cjsj,jysj,je,jyje,mj_id,ds_id", null);


        return null;
    }

    /**
     * 分析支付宝交易流水数据
     * 并清洗流水的交易时间的 时、日、周、月等标签
     *
     * @param userId 支付宝id
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public Object analyzeJyls(String userId, String xcbh, String token) throws IOException {
        String dsl = String.format(queryDsl, userId, xcbh);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);

        TjfxZfbJyls tjfxZfbJyls = new TjfxZfbJyls();
        tjfxZfbJyls.setUserId(userId);
        tjfxZfbJyls.setXcbh(xcbh);
        this.aggJyls(dslJson, tjfxZfbJyls, token);
        return tjfxZfbJyls;
    }

    /**
     * 分析支付宝交易对手数据
     * 并清洗流水的交易时间的 时、日、周、月等标签
     *
     * @param userId
     * @param xcbh
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public Object analyzeJyds(String userId, String xcbh, String token) throws IOException {
        List<TjfxZfbJyds> resultList = new ArrayList<>();
        String dsl = String.format(queryDsl, userId,xcbh);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);
        Set<String> jydsZh = new HashSet<>();

        JSONArray aggsJSONArray = dslJson.getJSONArray("aggs");
        aggsJSONArray.clear();
        //聚合对手账号
        JSONObject count = new JSONObject();
        count.put("groupName", "terms_ds");
        count.put("field", "ds_id");
        count.put("aggsType", 1);
        aggsJSONArray.add(count);
        JSONObject aggsObject = aggs(eqaConfig.getAggsUrl(),dslJson.toJSONString(),token);
        if(aggsObject!=null){
            JSONArray dsArray = aggsObject.getJSONArray("terms_ds");
            for (int i = 0; i < dsArray.size(); i++) {
                JSONObject cv = dsArray.getJSONObject(i);
                String dszh = cv.getString("key");
                if(!userId.equals(dszh) && !jydsZh.contains(dszh)){
                    jydsZh.add(dszh);
                }
            }
        }
        JSONArray conditions = dslJson.getJSONArray("conditions");
        JSONObject cond1 = new JSONObject();
        cond1.put("groupId", "group-field-1527089623689");
        cond1.put("groupType", "should");
        cond1.put("field", "ds_id");
        cond1.put("values", null);
        cond1.put("searchType", 1);
        cond1.put("dataType", 2);
        conditions.add(cond1);

        //统计对手
        jydsZh.forEach(dszh->{
            cond1.put("values", new String[]{dszh});
            TjfxZfbJyds jyds = new TjfxZfbJyds();
            jyds.setUserId(userId);
            jyds.setDfId(dszh);
            this.aggJyls(dslJson,jyds,token);
            resultList.add(jyds);
        });
        Collections.sort(resultList, new Comparator<TjfxZfbJyds>() {
            @Override
            public int compare(TjfxZfbJyds o1, TjfxZfbJyds o2) {
                if(o1.getLjjyje() > o2.getLjjyje()){
                    return -1;
                }else if(o1.getLjjyje() < o2.getLjjyje()){
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
     * @param userId
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public Object analyzeJyje(String userId, String xcbh, String token) throws IOException {
        String dsl = String.format(queryDsl, userId, xcbh);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);
        JSONArray aggsJSONArray = dslJson.getJSONArray("aggs");
        aggsJSONArray.clear();


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

    private TjfxJyls aggJyls(JSONObject dslJson, TjfxJyls jyls, String token) {
        JSONArray conditions = dslJson.getJSONArray("conditions");

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
