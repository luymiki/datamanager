package com.anluy.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

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
                case "qq":
                case "weixin":
                case "cft":
                case "zfb":
                case "yhzh":
                case "phone":
                case "imei":
                case "email":
                case "ip":{
                    String[] ss = value.toString().split(" |,|，");
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
     *
     * @param suspId
     */
    @Override
    public void analyze(String suspId,String token) throws IOException {
        Map suspObject = elasticsearchRestClient.get(this.getIndexName(), suspId, null, null);
        //dsl查询语句
        if (StringUtils.isBlank(queryDsl)) {
            queryDsl = IOUtils.toString(SuspiciousServiceImpl.class.getResourceAsStream("/dsl/eqa-query.json"));
        }

        //1、根据可疑人员来同步的
        //提取QQ号 qqreginfo：qq;email:qq.com;qqzone:qq；qqloginip：qq ；wxreginfo：qq
        this.tiquQQ(suspId, token,suspObject);

        //提取微信号 wxreginfo：weixin
        this.tiquWixin(suspId,token, suspObject);

        //提取财付通 cftreginfo：zh
        this.tiquCft(suspId,token, suspObject);

        //提取银行账号 cftreginfo：yhzh_list
        this.tiquYhzh(suspId,token, suspObject);

        //提取手机号 qqreginfo：dh;wxreginfo:dh;cftreginfo:dh ;huaduan:zjhm;
        this.tiquSjhm(suspId,token, suspObject);

        //提取电子邮箱 email：to_address; qqreginfo:email;wxreginfo:email
        this.tiquEmail(suspId,token, suspObject);

        //提取IP wxreginfo：ip_list[]; qqloginip:ip_list[]
        this.tiquIp(suspId,token, suspObject);

        //2、通过关联关系提取
        //提取IMEI xndw_sx：imei; xndw_wsk:imei，通过手机号和微信号提取
        this.tiquIMEI(suspId,token, suspObject);

    }

    /**
     * 查询es
     *
     * @return
     */
    private JSONObject get(String dsl,String token) {
        return getByDsl(eqaConfig.getQueryUrl(),dsl,token);
    }

    /**
     * 从数据map中获取数据
     *
     * @param suspObject
     * @param field
     * @return
     */
    private Set<String> getDataSet(Map suspObject, String field) {
        Set<String> nset = new HashSet<>();
        if(suspObject.get(field) instanceof String){
            String q = (String) suspObject.get(field);
            if (StringUtils.isNotBlank(q)) {
                nset.add(q);
            }
        }else if(suspObject.get(field) instanceof JSONArray){
            JSONArray q = (JSONArray) suspObject.get(field);
            if (q != null) {
                q.forEach(s -> {
                    if (StringUtils.isNotBlank((String) s)) {
                        nset.add((String) s);
                    }
                });
            }
        }

        return nset;
    }

    /**
     * 提取qq号码
     *
     * @param suspId
     * @param suspObject
     */
    private void tiquQQ(String suspId,String token, Map suspObject) {
        String json = String.format(queryDsl, "qqreginfo,email,qqzone,qqloginip,wxreginfo", suspId);
        JSONObject resultJson = get(json, token);
        //提取QQ号 qqreginfo：qq;email:qq.com;qqzone:qq；qqloginip：qq ；wxreginfo：qq
        if (resultJson != null && resultJson.getJSONObject("data") != null && resultJson.getJSONObject("data").getJSONArray("data") != null) {
            JSONArray dataList = resultJson.getJSONObject("data").getJSONArray("data");
            Set<String> qqset = this.getDataSet(suspObject, "qq");

            dataList.forEach(o -> {
                JSONObject jo = (JSONObject) o;
                switch ((String) jo.get("_index")) {
                    case "qqreginfo":
                    case "qqzone":
                    case "qqloginip":
                    case "wxreginfo": {
                        String qq = (String) jo.get("qq");
                        if (StringUtils.isNotBlank(qq) && !qqset.contains(qq)) {
                            qqset.add(qq);
                        }
                        break;
                    }
                    case "email": {
                        String to_address = (String) jo.get("to_address");
                        if (StringUtils.isNotBlank(to_address) && to_address.trim().toLowerCase().endsWith("qq.com")) {
                            String qq = to_address.trim().toLowerCase().replace("@qq.com", "");
                            if (StringUtils.isNotBlank(qq) && !qqset.contains(qq)) {
                                qqset.add(qq);
                            }
                        }
                        break;
                    }
                }
            });

            Map updateMap = new HashMap();
            updateMap.put("qq", qqset);
            suspObject.put("qq", JSON.toJSON(qqset));
            elasticsearchRestClient.update(updateMap, suspId, this.getIndexName());
        }

    }

    /**
     * 提取微信号
     *
     * @param suspId
     * @param suspObject
     */
    private void tiquWixin(String suspId,String token, Map suspObject) {
        JSON json = (JSON) JSON.parse(String.format(queryDsl, "wxreginfo", suspId));
        JSONObject resultJson = get(JSON.toJSONString(json), token);
        //提取微信号 wxreginfo：weixin
        if (resultJson != null && resultJson.getJSONObject("data") != null && resultJson.getJSONObject("data").getJSONArray("data") != null) {
            JSONArray dataList = resultJson.getJSONObject("data").getJSONArray("data");
            Set<String> weixinset = this.getDataSet(suspObject, "weixin");
            dataList.forEach(o -> {
                JSONObject jo = (JSONObject) o;
                switch ((String) jo.get("_index")) {
                    case "wxreginfo": {
                        String weixin = (String) jo.get("weixin");
                        if (StringUtils.isNotBlank(weixin) && !weixinset.contains(weixin)) {
                            weixinset.add(weixin);
                        }
                        break;
                    }
                }
            });

            Map updateMap = new HashMap();
            updateMap.put("weixin", weixinset);
            suspObject.put("weixin", JSON.toJSON(weixinset));
            elasticsearchRestClient.update(updateMap, suspId, this.getIndexName());
        }

    }

    /**
     * 提取财付通信息
     *
     * @param suspId
     * @param suspObject
     */
    private void tiquCft(String suspId,String token, Map suspObject) {
        JSON json = (JSON) JSON.parse(String.format(queryDsl, "cftreginfo", suspId));
        JSONObject resultJson = get(JSON.toJSONString(json), token);
        //提取财付通 cftreginfo：zh
        if (resultJson != null && resultJson.getJSONObject("data") != null && resultJson.getJSONObject("data").getJSONArray("data") != null) {
            JSONArray dataList = resultJson.getJSONObject("data").getJSONArray("data");
            Set<String> nset = this.getDataSet(suspObject, "cft");
            dataList.forEach(o -> {
                JSONObject jo = (JSONObject) o;
                switch ((String) jo.get("_index")) {
                    case "cftreginfo": {
                        String cft = (String) jo.get("zh");
                        if (StringUtils.isNotBlank(cft) && !nset.contains(cft)) {
                            nset.add(cft);
                        }
                        break;
                    }
                }
            });

            Map updateMap = new HashMap();
            updateMap.put("cft", nset);
            suspObject.put("cft", JSON.toJSON(nset));
            elasticsearchRestClient.update(updateMap, suspId, this.getIndexName());
        }

    }

    /**
     * 提取银行账号信息
     *
     * @param suspId
     * @param suspObject
     */
    private void tiquYhzh(String suspId, String token,Map suspObject) {
        JSON json = (JSON) JSON.parse(String.format(queryDsl, "cftreginfo", suspId));
        JSONObject resultJson = get(JSON.toJSONString(json),token);
        //提取银行账号 cftreginfo：yhzh_list
        if (resultJson != null && resultJson.getJSONObject("data") != null && resultJson.getJSONObject("data").getJSONArray("data") != null) {
            JSONArray dataList = resultJson.getJSONObject("data").getJSONArray("data");
            Set<String> nset = this.getDataSet(suspObject, "yhzh");
            dataList.forEach(o -> {
                JSONObject jo = (JSONObject) o;
                switch ((String) jo.get("_index")) {
                    case "cftreginfo": {
                        JSONArray value = (JSONArray) jo.get("yhzh_list");
                        if (value != null) {
                            value.forEach(s -> {
                                if (StringUtils.isNotBlank((String) s)) {
                                    nset.add((String) s);
                                }
                            });
                        }
                        break;
                    }
                }
            });

            Map updateMap = new HashMap();
            updateMap.put("yhzh", nset);
            suspObject.put("yhzh", JSON.toJSON(nset));
            elasticsearchRestClient.update(updateMap, suspId, this.getIndexName());
        }
    }

    /**
     * 提取手机号码信息
     *
     * @param suspId
     * @param suspObject
     */
    private void tiquSjhm(String suspId, String token,Map suspObject) {
        //还有huaduan没有实现
        JSON json = (JSON) JSON.parse(String.format(queryDsl, "qqreginfo,wxreginfo,cftreginfo", suspId));
        JSONObject resultJson = get(JSON.toJSONString(json), token);
        //提取手机号 qqreginfo：dh;wxreginfo:dh;cftreginfo:dh ;huaduan:zjhm;
        if (resultJson != null && resultJson.getJSONObject("data") != null && resultJson.getJSONObject("data").getJSONArray("data") != null) {
            JSONArray dataList = resultJson.getJSONObject("data").getJSONArray("data");
            Set<String> nset = this.getDataSet(suspObject, "phone");
            dataList.forEach(o -> {
                JSONObject jo = (JSONObject) o;
                switch ((String) jo.get("_index")) {
                    case "qqreginfo":
                    case "wxreginfo":
                    case "cftreginfo": {
                        String value = (String) jo.get("dh");
                        if (StringUtils.isNotBlank(value)) {
                            nset.add(value);
                        }
                        break;
                    }
                    case "huaduan": {
                        String value = (String) jo.get("zjhm");
                        if (StringUtils.isNotBlank(value)) {
                            nset.add(value);
                        }
                        break;
                    }
                }
            });

            Map updateMap = new HashMap();
            updateMap.put("phone", nset);
            suspObject.put("phone", JSON.toJSON(nset));
            elasticsearchRestClient.update(updateMap, suspId, this.getIndexName());
        }

    }

    /**
     * 提取邮箱信息
     *
     * @param suspId
     * @param suspObject
     */
    private void tiquEmail(String suspId, String token,Map suspObject) {
        JSON json = (JSON) JSON.parse(String.format(queryDsl, "email,qqreginfo,wxreginfo", suspId));
        JSONObject resultJson = get(JSON.toJSONString(json), token);
        //提取电子邮箱 email：to_address; qqreginfo:email;wxreginfo:email
        if (resultJson != null && resultJson.getJSONObject("data") != null && resultJson.getJSONObject("data").getJSONArray("data") != null) {
            JSONArray dataList = resultJson.getJSONObject("data").getJSONArray("data");
            Set<String> nset = this.getDataSet(suspObject, "email");
            dataList.forEach(o -> {
                JSONObject jo = (JSONObject) o;
                switch ((String) jo.get("_index")) {
                    case "qqreginfo":
                    case "wxreginfo": {
                        String value = (String) jo.get("email");
                        if (StringUtils.isNotBlank(value)) {
                            nset.add(value);
                        }
                        break;
                    }
                    case "email": {
                        String value = (String) jo.get("to_address");
                        if (StringUtils.isNotBlank(value)) {
                            nset.add(value);
                        }
                        break;
                    }
                }
            });

            Map updateMap = new HashMap();
            updateMap.put("email", nset);
            suspObject.put("email", JSON.toJSON(nset));
            elasticsearchRestClient.update(updateMap, suspId, this.getIndexName());
        }

    }
    /**
     * 提取IP
     *
     * @param suspId
     * @param suspObject
     */
    private void tiquIp(String suspId,String token, Map suspObject) {
        JSON json = (JSON) JSON.parse(String.format(queryDsl, "wxreginfo,qqloginip", suspId));
        JSONObject resultJson = get(JSON.toJSONString(json),token);
        //提取IP wxreginfo：ip_list[]; qqloginip:ip_list[]
        if (resultJson != null && resultJson.getJSONObject("data") != null && resultJson.getJSONObject("data").getJSONArray("data") != null) {
            JSONArray dataList = resultJson.getJSONObject("data").getJSONArray("data");
            Set<String> nset = this.getDataSet(suspObject, "ip");
            dataList.forEach(o -> {
                JSONObject jo = (JSONObject) o;
                switch ((String) jo.get("_index")) {
                    case "wxloginip":
                    case "qqloginip": {
                        JSONArray value = (JSONArray) jo.get("ip_list");
                        if (value !=null && !value.isEmpty()) {
                            value.forEach(v->{
                                nset.add((String) v);
                            });
                        }
                        break;
                    }
                }
            });

            Map updateMap = new HashMap();
            updateMap.put("ip", nset);
            suspObject.put("ip", JSON.toJSON(nset));
            elasticsearchRestClient.update(updateMap, suspId, this.getIndexName());
        }

    }

    /**
     * 提取IMEI信息
     *
     * @param suspId
     * @param suspObject
     */
    private void tiquIMEI(String suspId,String token, Map suspObject) throws IOException {
        JSONArray dataList = new JSONArray();
        //提取提取IMEI xndw_sx：imei; xndw_wsk:imei，通过手机号和微信号提取

        String dslStr = IOUtils.toString(SuspiciousServiceImpl.class.getResourceAsStream("/dsl/eqa-query-sdl.json"));
        String dsl = String.format(dslStr, "xndw_wsk");
        JSONObject jo1 = (JSONObject) JSON.parse(dsl);
        JSONArray conditions = jo1.getJSONArray("conditions");
        JSONObject con1 = (JSONObject) conditions.remove(0);
        JSONObject con2 = ObjectUtils.clone(con1);
        JSONObject con3 = ObjectUtils.clone(con1);
        JSONObject con4 = ObjectUtils.clone(con1);
        if (suspObject.get("weixin") != null && ((JSONArray) suspObject.get("weixin")).size() > 0) {
            con1.put("field", "wechat_id");
            con1.put("values", suspObject.get("weixin"));
            conditions.add(con1);
        }
        if (suspObject.get("phone") != null && ((JSONArray) suspObject.get("phone")).size() > 0) {
            con2.put("field", "mobilephone");
            con2.put("values", suspObject.get("phone"));
            conditions.add(con2);
        }
        if (suspObject.get("qq") != null && ((JSONArray) suspObject.get("qq")).size() > 0) {
            con3.put("field", "qq");
            con3.put("values", suspObject.get("qq"));
            conditions.add(con3);
        }
        if (suspObject.get("email") != null && ((JSONArray) suspObject.get("email")).size() > 0) {
            con4.put("field", "email");
            con4.put("values", suspObject.get("email"));
            conditions.add(con4);
        }
        if (!conditions.isEmpty()) {
            queryByDsl(eqaConfig.getQueryUrl(),JSON.toJSONString(jo1),token,dataList);
        }

        dsl = String.format(dslStr, "xndw_sx");
        jo1 = (JSONObject) JSON.parse(dsl);
        conditions = jo1.getJSONArray("conditions");
        con1 = (JSONObject) conditions.remove(0);
        con2 = ObjectUtils.clone(con1);
        if (suspObject.get("email") != null && ((JSONArray) suspObject.get("email")).size() > 0) {
            con1.put("field", "email");
            con1.put("values", suspObject.get("email"));
            conditions.add(con1);
        }
        if (suspObject.get("phone") != null && ((JSONArray) suspObject.get("phone")).size() > 0) {
            con2.put("field", "phone_num");
            con2.put("values", suspObject.get("phone"));
            conditions.add(con2);
        }

        if (!conditions.isEmpty()) {
            queryByDsl(eqaConfig.getQueryUrl(),JSON.toJSONString(jo1),token,dataList);
        }

        if (!dataList.isEmpty()) {
            Set<String> mieiset = this.getDataSet(suspObject, "imei");
            Set<String> misiset = this.getDataSet(suspObject, "imsi");
            dataList.forEach(o -> {
                JSONObject jo = (JSONObject) o;
                switch ((String) jo.get("_index")) {
                    case "xndw_wsk": {
                        String value = (String) jo.get("imei");
                        if (StringUtils.isNotBlank(value)) {
                            mieiset.add(value);
                        }
                        break;
                    }
                    case "xndw_sx": {
                        String value = (String) jo.get("imei");
                        String value2 = (String) jo.get("imsi");
                        if (StringUtils.isNotBlank(value)) {
                            mieiset.add(value);
                        }
                        if (StringUtils.isNotBlank(value2)) {
                            misiset.add(value2);
                        }
                        break;
                    }
                }
            });

            Map updateMap = new HashMap();
            updateMap.put("imei", mieiset);
            suspObject.put("imei", JSON.toJSON(mieiset));
            updateMap.put("imsi", misiset);
            suspObject.put("imsi", JSON.toJSON(misiset));
            elasticsearchRestClient.update(updateMap, suspId, this.getIndexName());
        }
    }
}
