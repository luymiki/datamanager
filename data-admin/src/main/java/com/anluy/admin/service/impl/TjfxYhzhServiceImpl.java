package com.anluy.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.EqaConfig;
import com.anluy.admin.entity.TjfxCftJyls;
import com.anluy.admin.entity.TjfxJyds;
import com.anluy.admin.entity.TjfxYhzhJyds;
import com.anluy.admin.entity.TjfxYhzhJyls;
import com.anluy.admin.service.TjfxCftService;
import com.anluy.admin.service.TjfxYhzhService;
import com.anluy.commons.dao.BaseDAO;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.service.BaseServiceImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能说明：银行流水统计分析
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
@Service
public class TjfxYhzhServiceImpl extends BaseServiceImpl implements TjfxYhzhService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TjfxYhzhServiceImpl.class);
    @Resource
    private EqaConfig eqaConfig;

    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private CacheManager cacheManager;

    private String queryNyyhDsl;
    private String queryQtyhDsl;

    public TjfxYhzhServiceImpl() {
        //dsl查询语句
        if (StringUtils.isBlank(queryNyyhDsl)) {
            try {
                queryNyyhDsl = IOUtils.toString(TjfxYhzhServiceImpl.class.getResourceAsStream("/dsl/eqa-queryYhzhJyls-nyyh.json"));
            } catch (IOException e) {
                LOGGER.error("DSL文件加载错误", e);
            }
        }
        //dsl查询语句
        if (StringUtils.isBlank(queryQtyhDsl)) {
            try {
                queryQtyhDsl = IOUtils.toString(TjfxYhzhServiceImpl.class.getResourceAsStream("/dsl/eqa-queryYhzhJyls-qtyh.json"));
            } catch (IOException e) {
                LOGGER.error("DSL文件加载错误", e);
            }
        }
    }

    private String getDsl(String ssyh,String kh,String zh){
        if("农业银行".equals(ssyh)){
            return String.format(queryNyyhDsl, kh);
        }
        return String.format(queryQtyhDsl, ssyh,kh,zh);
    }

    /**
     * 分析银行流水交易流水数据
     * 并清洗流水的交易时间的 时、日、周、月等标签
     *
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public Object analyzeJyls(String ssyh,String kh,String zh, String jyjeRange, String dsId, String zcType, String token) throws IOException {
        String dsl = this.getDsl(ssyh,kh,zh);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);

        TjfxYhzhJyls tjfxCftJyls = new TjfxYhzhJyls();
        tjfxCftJyls.setSsyh(ssyh);
        tjfxCftJyls.setKh(kh);
        tjfxCftJyls.setZh(zh);
        this.aggJyls(dslJson, tjfxCftJyls, jyjeRange, dsId, zcType, token);
        return tjfxCftJyls;
    }

    /**
     * 分析银行流水交易对手数据
     * 并清洗流水的交易时间的 时、日、周、月等标签
     *
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public Object analyzeJyds(String ssyh,String kh,String zh, String zcType, String token) throws IOException {
        String dsl = this.getDsl(ssyh,kh,zh);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);
        TjfxYhzhJyds jyds = new TjfxYhzhJyds();
        jyds.setSsyh(ssyh);
        jyds.setZh(zh);
        jyds.setKh(kh);
        return this.analyzeJyds(dslJson,jyds,zcType,token);
    }


    /**
     * 统计交易金额区间
     *
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public Object analyzeJyje(String ssyh,String kh,String zh, String dsId, String zcType, String token) throws IOException {
        String dsl = this.getDsl(ssyh,kh,zh);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);
        return this.analyzeJyje(dslJson,dsId,zcType,token);
    }


    /**
     * 统计交易金额区间
     *
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public Object analyzeZc100(String ssyh,String kh,String zh, String dsId, String token) throws IOException {
        String dsl = this.getDsl(ssyh,kh,zh);
        JSONObject dslJson = (JSONObject) JSON.parse(dsl);
        return this.analyzeZc100(eqaConfig.getAggsUrl(),dslJson,dsId,token);
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
