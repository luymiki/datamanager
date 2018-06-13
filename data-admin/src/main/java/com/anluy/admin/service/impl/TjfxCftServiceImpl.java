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
import com.anluy.commons.dao.BaseDAO;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.service.BaseServiceImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * 功能说明：财付通统计分析
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
@Service
public class TjfxCftServiceImpl extends BaseServiceImpl implements TjfxCftService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TjfxCftServiceImpl.class);

    @Resource
    private EqaConfig eqaConfig;

    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    private String queryDsl;
    private String queryDslForIntegrated;

    public TjfxCftServiceImpl(){
        //dsl查询语句
        if (StringUtils.isBlank(queryDsl)) {
            try {
                queryDsl = IOUtils.toString(TjfxCftServiceImpl.class.getResourceAsStream("/dsl/eqa-queryCftJyls.json"));
            } catch (IOException e) {
                LOGGER.error("DSL文件加载错误",e);
            }
        }
        //dsl查询语句
        if (StringUtils.isBlank(queryDslForIntegrated)) {
            try {
                queryDslForIntegrated = IOUtils.toString(TjfxCftServiceImpl.class.getResourceAsStream("/dsl/eqa-queryCftData.json"));
            } catch (IOException e) {
                LOGGER.error("DSL文件加载错误",e);
            }
        }
    }

    /**
     * 整合数据
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
                        if(StringUtils.isBlank(ds_id)){
                            if(StringUtils.isBlank(fsf) && StringUtils.isBlank(jsf)){
                                map.put("ds_id","-");
                            }else if("出".equals(jdlx)) {
                                if(StringUtils.isBlank(jsf))
                                    map.put("ds_id","-");
                                else
                                    map.put("ds_id", jsf);
                            }else {
                                if(StringUtils.isBlank(fsf))
                                    map.put("ds_id","-");
                                else
                                    map.put("ds_id", fsf);
                            }
                            dataList.add(map);
                        }
                        if(dataList.size() == 500){
                            elasticsearchRestClient.batchUpdate(dataList,"cfttrades");
                            dataList.clear();
                        }
                    }
                    if( !dataList.isEmpty()){
                        elasticsearchRestClient.batchUpdate(dataList,"cfttrades");
                    }
                }
            }
        }, "cfttrades", "id,zh,jdlx,jsf,fsf,ds_id", null);

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
    public Object analyzeJyls(String cftId,String jyjeRange,String dsId, String token) throws IOException {
        String dsl = String.format(queryDsl, cftId);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);

        TjfxCftJyls tjfxCftJyls = new TjfxCftJyls();
        tjfxCftJyls.setCftId(cftId);
        this.aggJyls(dslJson,tjfxCftJyls,jyjeRange,dsId,token);
        return tjfxCftJyls;
    }

    /**
     * 分析财付通交易对手数据
     * 并清洗流水的交易时间的 时、日、周、月等标签
     * @param cftId
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public Object analyzeJyds(String cftId,String token) throws IOException {
        List<TjfxCftJyds> resultList = new ArrayList<>();
        String dsl = String.format(queryDsl, cftId);
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
                jydsZh.add(dszh);
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
            TjfxCftJyds jyds = new TjfxCftJyds();
            jyds.setDfId(dszh);
            this.aggJyls(dslJson,jyds,null,null,token);
            resultList.add(jyds);
        });
        Collections.sort(resultList, new Comparator<TjfxCftJyds>() {
            @Override
            public int compare(TjfxCftJyds o1, TjfxCftJyds o2) {
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
     * @param cftId
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public Object analyzeJyje(String cftId,String dsId, String token) throws IOException {
        String dsl = String.format(queryDsl, cftId);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);
        JSONArray aggsJSONArray = dslJson.getJSONArray("aggs");
        aggsJSONArray.clear();

        JSONArray conditions = dslJson.getJSONArray("conditions");
        if(StringUtils.isNotBlank(dsId)){
            JSONObject cond = new JSONObject();
            cond.put("field", "ds_id");
            cond.put("values", new String[]{dsId});
            cond.put("searchType", 1);
            cond.put("dataType", 2);
            conditions.add(cond);
        }
        List<Double[]> rangeList = new ArrayList<>();
        rangeList.add(new Double[]{0.00,10.00});
        rangeList.add(new Double[]{10.00,50.00});
        rangeList.add(new Double[]{50.00,100.00});
        rangeList.add(new Double[]{100.00,500.00});
        rangeList.add(new Double[]{500.00,1000.00});
        rangeList.add(new Double[]{1000.00,5000.00});
        rangeList.add(new Double[]{5000.00,10000.00});
        rangeList.add(new Double[]{10000.00,50000.00});
        rangeList.add(new Double[]{50000.00,null});
        List<Map> values = new ArrayList<>();
        for (Double[] range :rangeList             ) {
            Map<String,Double> map = new HashMap<>();
            map.put("from",range[0]);
            map.put("to",range[1]);
            values.add(map);
        }
        //
        JSONObject max = new JSONObject();
        max.put("groupName", "group_jyje");
        max.put("field", "jyje");
        max.put("values", values);
        max.put("aggsType", 8);
        aggsJSONArray.add(max);
        JSONObject aggsObj = aggs(eqaConfig.getAggsUrl(),JSON.toJSONString(dslJson), token);
        return aggsObj;
    }

    private TjfxJyls aggJyls(JSONObject dslJson, TjfxJyls jyls,String jyjeRange,String dsId, String token){
        JSONArray conditions = dslJson.getJSONArray("conditions");
        if(StringUtils.isNotBlank(jyjeRange)){
            JSONObject cond = new JSONObject();
            cond.put("field", "jyje");
            cond.put("values", jyjeRange.replace("\\*","").split("-"));
            cond.put("searchType", 6);
            cond.put("dataType", 3);
            conditions.add(cond);
        }
        if(StringUtils.isNotBlank(dsId)){
            JSONObject cond = new JSONObject();
            cond.put("field", "ds_id");
            cond.put("values", new String[]{dsId});
            cond.put("searchType", 1);
            cond.put("dataType", 2);
            conditions.add(cond);
        }

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
//        //{"field":"shmc","searchType":"2","dataType":"2","values":["微信红包","QQ红包"],"groupType":"not"}
//        cond3.put("field", "shmc");
//        cond3.put("values", new String[]{"微信红包", "QQ红包"});
//        cond3.put("searchType", 2);
//        cond3.put("dataType", 2);
//        cond3.put("groupType", "not");
//        aggsObj = aggsJyje(dslJson, token);
//        if (aggsObj != null) {
//            jyls.setZzzdjyje(aggsObj.getDouble("max_jyje"));
//            jyls.setZzzxjyje(aggsObj.getDouble("min_jyje"));
//            jyls.setZzpjjyje(aggsObj.getDouble("avg_jyje"));
//            jyls.setZzljjyje(aggsObj.getDouble("sum_jyje"));
//            jyls.setZzljjybs(aggsObj.getInteger("count_jyje"));
//        }
//        JSONObject cond4 = new JSONObject();
//        cond4.put("field", "jdlx");
//        cond4.put("values", new String[]{"出"});
//        cond4.put("searchType", 1);
//        cond4.put("dataType", 2);
//        conditions.add(cond4);
//        aggsObj = aggsJyje(dslJson, token);
//        if (aggsObj != null) {
//            jyls.setZzzdzcje(aggsObj.getDouble("max_jyje"));
//            jyls.setZzzxzcje(aggsObj.getDouble("min_jyje"));
//            jyls.setZzljzcje(aggsObj.getDouble("sum_jyje"));
//            jyls.setZzljzcbs(aggsObj.getInteger("count_jyje"));
//        }
//        cond4.put("values", new String[]{"入"});
//        aggsObj = aggsJyje(dslJson, token);
//        if (aggsObj != null) {
//            jyls.setZzzdzrje(aggsObj.getDouble("max_jyje"));
//            jyls.setZzzxzrje(aggsObj.getDouble("min_jyje"));
//            jyls.setZzljzrje(aggsObj.getDouble("sum_jyje"));
//            jyls.setZzljzrbs(aggsObj.getInteger("count_jyje"));
//        }
//        conditions.remove(conditions.size()-1);
        conditions.remove(conditions.size()-1);
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
        return aggs(eqaConfig.getAggsUrl(),dslJson.toJSONString(),token);
    }

    /**
     * 统计时间
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
        return aggs(eqaConfig.getAggsUrl(),dslJson.toJSONString(),token);
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
