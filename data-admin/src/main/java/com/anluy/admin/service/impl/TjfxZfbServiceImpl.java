package com.anluy.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.EqaConfig;
import com.anluy.admin.entity.*;
import com.anluy.admin.service.TjfxCftService;
import com.anluy.admin.service.TjfxZfbService;
import com.anluy.admin.utils.MD5;
import com.anluy.commons.dao.BaseDAO;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.service.BaseServiceImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.support.SimpleValueWrapper;
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
    private static final String CACHE_NAME ="Eqa-Aggs-Cache";
    @Resource
    private EqaConfig eqaConfig;

    @Resource
    private CacheManager cacheManager;

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
        String dsl = String.format(queryDslForIntegrated, xcbh);
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

                        Object jyje =map.get("jyje");
                        String df_user_id = (String) map.get("df_user_id");
                        String ds_id = (String) map.get("ds_id");
                        Double je = null;
                        Object je0 = map.get("je");
                        if (je0 instanceof BigDecimal) {
                            je = ((BigDecimal) je0).doubleValue();
                        } else if (je0 instanceof Double) {
                            je = ((Double) je0);
                        } else {
                            je = Double.valueOf(je0.toString());
                        }
                        Double zc100 = (Double) map.get("zc100");

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
                        if (zc100 == null) {
                            update = true;
                            if (jyje != null) {
                                Double mod = je % 100;
                                map.put("zc100", mod);
                            } else {
                                map.put("zc100", -1);
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
        }, "zfbzhinfo", "id,sjbj,jdlx,jycjsj,jysj,je,jyje,df_user_id,ds_id,zc100", null);

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
                        Object jyje =  map.get("jyje");
                        String yhzh = (String) map.get("yhzh");
                        String ds_id = (String) map.get("ds_id");
                        Double je = null;
                        Object je0 = map.get("je");
                        if (je0 instanceof BigDecimal) {
                            je = ((BigDecimal) je0).doubleValue();
                        } else if (je0 instanceof Double) {
                            je = ((Double) je0);
                        } else {
                            je = Double.valueOf(je0.toString());
                        }
                        Double zc100 = (Double) map.get("zc100");
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
                        if (zc100 == null) {
                            update = true;
                            if (jyje != null) {
                                Double mod = je % 100;
                                map.put("zc100", mod);
                            } else {
                                map.put("zc100", -1);
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
        }, "zfbtxinfo", "id,jdlx,sqsj,jysj,je,jyje,yhzh,ds_id,zc100", null);

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
                        Object jyje = map.get("jyje");
                        String skf_id = (String) map.get("skf_id");
                        String ds_id = (String) map.get("ds_id");
                        Double je = null;
                        Object je0 = map.get("je");
                        if (je0 instanceof BigDecimal) {
                            je = ((BigDecimal) je0).doubleValue();
                        } else if (je0 instanceof Double) {
                            je = ((Double) je0);
                        } else {
                            je = Double.valueOf(je0.toString());
                        }
                        Double zc100 = (Double) map.get("zc100");

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
                        if (zc100 == null) {
                            update = true;
                            if (jyje != null) {
                                Double mod = je % 100;
                                map.put("zc100", mod);
                            } else {
                                map.put("zc100", -1);
                            }
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
        }, "zfbzzinfo", "id,fkf_id,jdlx,dzsj,jysj,je,jyje,skf_id,ds_id,zc100", null);

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
                        Object jyje =  map.get("jyje");
                        String mj_id = (String) map.get("mj_id");
                        String ds_id = (String) map.get("ds_id");
                        Double je = null;
                        Object je0 = map.get("je");
                        if (je0 instanceof BigDecimal) {
                            je = ((BigDecimal) je0).doubleValue();
                        } else if (je0 instanceof Double) {
                            je = ((Double) je0);
                        } else {
                            je = Double.valueOf(je0.toString());
                        }
                        Double zc100 = (Double) map.get("zc100");
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
                        if (zc100 == null) {
                            update = true;
                            if (jyje != null) {
                                Double mod = je % 100;
                                map.put("zc100", mod);
                            } else {
                                map.put("zc100", -1);
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
        }, "zfbjyjlinfo", "id,jdlx,cjsj,jysj,je,jyje,mj_id,ds_id,zc100", null);


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
    public Object analyzeJyls(String userId, String xcbh,String jyjeRange,String dsId,  String zcType, String token) throws IOException {
        String dsl = String.format(queryDsl, xcbh);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);

        TjfxZfbJyls tjfxZfbJyls = new TjfxZfbJyls();
        tjfxZfbJyls.setUserId(userId);
        tjfxZfbJyls.setXcbh(xcbh);
        this.aggJyls(dslJson, tjfxZfbJyls,jyjeRange,dsId,zcType, token);
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
    public Object analyzeJyds(String userId, String xcbh, String zcType,  String token) throws IOException {
        String dsl = String.format(queryDsl, xcbh);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);
        TjfxZfbJyds jyds = new TjfxZfbJyds();
        jyds.setUserId(userId);
        jyds.setXcbh(xcbh);
        return this.analyzeJyds(dslJson,jyds,zcType,token);
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
    public Object analyzeJyje(String userId, String xcbh,String dsId, String zcType,  String token) throws IOException {
        String dsl = String.format(queryDsl, xcbh);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);
        return this.analyzeJyje(dslJson,dsId,zcType,token);
    }


    /**
     * 统计能被100整除的数据
     * @param userId
     * @param xcbh
     * @param dsId
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public Object analyzeJyjeZc100(String userId, String xcbh, String dsId, String token) throws IOException {
        String dsl = String.format(queryDsl, xcbh);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);
        return this.analyzeZc100(eqaConfig.getAggsUrl(),dslJson,dsId,token);
    }

    @Override
    public String getAggUrl() {
        return eqaConfig.getAggsUrl();
    }

    @Override
    public CacheManager getCcacheManager() {
        return cacheManager;
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
