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
        tjfxCftJyls.setId(UUID.randomUUID().toString());
        tjfxCftJyls.setCftId(cftId);
        this.aggJyls(dslJson, tjfxCftJyls, jyjeRange, dsId, zcType, token);
        cacheManager.getCache(CACHE_NAME).put(tjfxCftJyls.getId(),JSON.toJSON(tjfxCftJyls));
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
        String dsl = String.format(queryDsl, cftId);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);
        TjfxCftJyds jyds = new TjfxCftJyds();
        jyds.setCftId(cftId);
        Object obj =  this.analyzeJyds(dslJson,jyds,zcType,token);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",UUID.randomUUID().toString());
        jsonObject.put("data",obj);
        cacheManager.getCache(CACHE_NAME).put(jsonObject.get("id"),JSON.toJSON(obj));
        return jsonObject;
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
        JSONObject jsonObject = (JSONObject) this.analyzeJyje(dslJson,dsId,zcType,token);
        jsonObject.put("id",UUID.randomUUID().toString());
        cacheManager.getCache(CACHE_NAME).put(jsonObject.get("id"),jsonObject);
        return jsonObject;
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
        JSONObject jsonObject =  (JSONObject) this.analyzeZc100(eqaConfig.getAggsUrl(),dslJson,dsId,token);
        jsonObject.put("id",UUID.randomUUID().toString());
        cacheManager.getCache(CACHE_NAME).put(jsonObject.get("id"),jsonObject);
        return jsonObject;
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

    @Override
    public String getAggUrl() {
        return eqaConfig.getAggsUrl();
    }

    @Override
    public CacheManager getCcacheManager() {
        return cacheManager;
    }
}
