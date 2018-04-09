package com.anluy.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.EqaConfig;
import com.anluy.admin.entity.Suspicious;
import com.anluy.admin.mapper.SuspiciousMapper;
import com.anluy.admin.service.SuspiciousService;
import com.anluy.admin.utils.HTTPUtils;
import com.anluy.commons.dao.BaseDAO;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.service.BaseServiceImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Spliterators;

/**
 * 功能说明：嫌疑人信息管理
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
@Service
@Transactional
public class SuspiciousServiceImpl extends BaseServiceImpl<String, Suspicious> implements SuspiciousService {

    @Resource
    private SuspiciousMapper suspiciousMapper;

    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private EqaConfig eqaConfig;

    private String queryDsl;

    public Object formater(String field, Object value) {
        if (value != null) {
            switch (field) {
                case "create_time":
                case "modify_time": {
                    return DateFormatUtils.format((Date) value, "yyyy-MM-dd HH:mm:ss");
                }
                case "qq":
                case "weixin":
                case "cft":
                case "zfb":
                case "yhzh":
                case "phone":
                case "imei":
                case "glry": {
                    String [] ss = value.toString().split(" |,|，");
                    return ss;
                }
            }
        }
        return value;
    }

    @Override
    public BaseDAO getBaseDAO() {
        return suspiciousMapper;
    }

    @Override
    public ElasticsearchRestClient getElasticsearchRestClient() {
        return elasticsearchRestClient;
    }

    @Override
    public String getIndexName() {
        return "suspicious";
    }

    /**
     * 分析提取数据
     * @param suspId
     */
    @Override
    public void analyze(String suspId) throws IOException {
        //dsl查询语句
        if(StringUtils.isBlank(queryDsl)){
            queryDsl = IOUtils.toString(SuspiciousServiceImpl.class.getResourceAsStream("/dsl/eqa-query.json"));
        }

        //1、根据可疑人员来同步的
        //提取QQ号 qqreginfo：qq;email:qq.com;qqzone:qq；qqloginip：qq ；wxreginfo：qq
        Map params = new HashMap<>();
        params.put("pageNum","1");
        params.put("pageSize","10000");
        params.put("paramsStr", String.format(queryDsl,"qqreginfo,email,qqzone,qqloginip,wxreginfo",suspId));
        JSONObject resultJson = HTTPUtils.getJSONObjectByPost(eqaConfig.getQueryUrl(),params,"utf-8");
        System.out.println(resultJson);

        //提取微信号 wxreginfo：weixin


        //提取财付通 cftreginfo：zh

        //提取银行账号 cftreginfo：yhzh_list


        //提取手机号 qqreginfo：dh;wxreginfo:dh;cftreginfo:dh ;huaduan:zjhm;

        //提取电子邮箱 email：to_address; qqreginfo:email;wxreginfo:email

        //2、通过关联关系提取

    }
}
