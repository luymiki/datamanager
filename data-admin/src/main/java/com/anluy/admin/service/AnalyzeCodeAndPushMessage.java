package com.anluy.admin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 功能说明：分析码值，并推送消息
 * <p>
 * Created by hc.zeng on 2018/8/28.
 */
@Component
public class AnalyzeCodeAndPushMessage {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzeCodeAndPushMessage.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private MessagingManager messagingManager;

    private static ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static final int ANALYZE_TYPE_QQ = 1;
    public static final int ANALYZE_TYPE_WEIXIN = 2;
    public static final int ANALYZE_TYPE_PHONE = 3;
    public static final int ANALYZE_TYPE_YHZH = 4;
    public static final int ANALYZE_TYPE_IP = 5;
    public static final int ANALYZE_TYPE_EMAIL = 6;

    /**
     * 比对数据
     *
     * @param mapList
     * @param keys
     * @param analyzeType
     */
    public void analyze(List<Map> mapList, int analyzeType, String... keys) {
        if (keys != null && keys.length > 0) {
            for (String key : keys) {
                mapList.forEach(map -> {
                    String keyCode = (String) map.get(key);
                    if(StringUtils.isBlank(keyCode)){
                        return;
                    }
                    JSONObject jo = (JSONObject) JSON.parse(JSON.toJSONString(map));
                    jo.put("keyCode", keyCode);
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            analyze(jo, analyzeType);
                        }
                    });

                });
            }
        }
    }
    /**
     * 比对数据
     *
     * @param map
     * @param keys
     * @param analyzeType
     */
    public void analyze(Map map, int analyzeType, String... keys) {
        if (keys != null && keys.length > 0) {
            for (String key : keys) {
                String keyCode = (String) map.get(key);
                if(StringUtils.isBlank(keyCode)){
                    continue;
                }
                JSONObject jo = (JSONObject) JSON.parse(JSON.toJSONString(map));
                jo.put("keyCode", keyCode);
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        analyze(jo, analyzeType);
                    }
                });
            }
        }

    }

    /**
     * 比对数据
     *
     * @param dataMap {"file_id":"","keyCode":"376160680",.........}
     */
    private void analyze(Map dataMap, int analyzeType) {
        switch (analyzeType) {
            case ANALYZE_TYPE_QQ: {
                this.analyzeQQ(dataMap);
                break;
            }
            case ANALYZE_TYPE_WEIXIN: {
                this.analyzeWeiXin(dataMap);
                break;
            }
            case ANALYZE_TYPE_PHONE: {
                this.analyzePhone(dataMap);
                break;
            }
            case ANALYZE_TYPE_YHZH: {
                this.analyzeYhzh(dataMap);
                break;
            }
            case ANALYZE_TYPE_IP: {
                this.analyzeIp(dataMap);
                break;
            }
            case ANALYZE_TYPE_EMAIL: {
                this.analyzeEmail(dataMap);
                break;
            }
            default: {
//                this.analyzeQQ(dataMap);
//                this.analyzeWeiXin(dataMap);
//                this.analyzePhone(dataMap);
//                this.analyzeYhzh(dataMap);
//                this.analyzeIp(dataMap);
//                this.analyzeEmail(dataMap);
            }
        }

    }

    /**
     * 比对QQ
     *
     * @param dataMap
     */
    public void analyzeQQ(Map dataMap) {
        String keyCode = (String) dataMap.get("keyCode");
        String fileId = (String) dataMap.get("file_id");
        try {
            int total = 0;
            total += this.query("qq", keyCode, fileId, "qqreginfo");
            total += this.query("jrqh", keyCode, fileId, "qqreginfo");
            total += this.query("cjqh", keyCode, fileId, "qqreginfo");
            total += this.query("qqhy", keyCode, fileId, "qqreginfo");
//            total += this.query("qq", keyCode, fileId, "qqloginip");
            total += this.query("qq", keyCode, fileId, "wxreginfo");
            total += this.query("qq", keyCode, fileId, "wxlxr");
            total += this.query("qq", keyCode, fileId, "xndw_wsk");
            total += this.query("fsf", keyCode, fileId, "cfttrades");
            total += this.query("jsf", keyCode, fileId, "cfttrades");
            if (total > 0) {
                dataMap.put("total", total);
                dataMap.put("tips", String.format("根据[%s]比中QQ信息%s条", keyCode, total));
                messagingManager.pushMessage(dataMap);
            }
        } catch (IOException e) {
            LOGGER.error(String.format("比对失败[%s]", keyCode), e);
        }
    }

    /**
     * 比对微信
     *
     * @param dataMap
     */
    public void analyzeWeiXin(Map dataMap) {
        String keyCode = (String) dataMap.get("keyCode");
        String fileId = (String) dataMap.get("file_id");
        try {
            int total = 0;
            total += this.query("weixin", keyCode, fileId, "wxreginfo");
            total += this.query("weixin", keyCode, fileId, "wxlxr");
            total += this.query("zh", keyCode, fileId, "wxlxr");
            total += this.query("weixin", keyCode, fileId, "wxqun");
            total += this.query("weixin", keyCode, fileId, "wxloginip");
            total += this.query("fsf", keyCode, fileId, "cfttrades");
            total += this.query("jsf", keyCode, fileId, "cfttrades");
            if (total > 0) {
                dataMap.put("total", total);
                dataMap.put("tips", String.format("根据[%s]比中微信数据%s条", keyCode, total));
                messagingManager.pushMessage(dataMap);
            }
        } catch (IOException e) {
            LOGGER.error(String.format("比对失败[%s]", keyCode), e);
        }
    }

    /**
     * 比对电话号码
     *
     * @param dataMap
     */
    public void analyzePhone(Map dataMap) {
        String keyCode = (String) dataMap.get("keyCode");
        String fileId = (String) dataMap.get("file_id");
        try {
            int total = 0;
            total += this.query("dh", keyCode, fileId, "wxreginfo");
            total += this.query("dh", keyCode, fileId, "wxlxr");
            total += this.query("zjhm", keyCode, fileId, "huaduan");
            total += this.query("ddhm", keyCode, fileId, "huaduan_list");
            total += this.query("dh", keyCode, fileId, "cftreginfo");
            total += this.query("dlsj", keyCode, fileId, "zfbreginfo");
            total += this.query("bdsj", keyCode, fileId, "zfbreginfo");
            total += this.query("phone_num", keyCode, fileId, "xndw_sx");
            total += this.query("lxdh", keyCode, fileId, "yhzh_khxx");
            total += this.query("lxsj", keyCode, fileId, "yhzh_khxx");
            total += this.query("sjhm", keyCode, fileId, "email_reg");
            if (total > 0) {
                dataMap.put("total", total);
                dataMap.put("tips", String.format("根据[%s]比中电话号码%s条", keyCode, total));
                messagingManager.pushMessage(dataMap);
            }
        } catch (IOException e) {
            LOGGER.error(String.format("比对失败[%s]", keyCode), e);
        }
    }

    /**
     * 比对银行账号
     *
     * @param dataMap
     */
    public void analyzeYhzh(Map dataMap) {
        String keyCode = (String) dataMap.get("keyCode");
        String fileId = (String) dataMap.get("file_id");
        try {
            int total = 0;
            total += this.query("yhzh_list", keyCode, fileId, "cftreginfo");
            total += this.query("yhzh_list", keyCode, fileId, "zfbreginfo");
            total += this.query("yhzh", keyCode, fileId, "zfbtxinfo");
            total += this.query("kh", keyCode, fileId, "yhzh_khxx");
            total += this.query("zh", keyCode, fileId, "yhzh_khxx");
            total += this.query("dfzh", keyCode, fileId, "yhzh_jyls");
            total += this.query("dfkh", keyCode, fileId, "yhzh_jyls");
            if (total > 0) {
                dataMap.put("total", total);
                dataMap.put("tips", String.format("根据[%s]比中银行账号%s条", keyCode, total));
                messagingManager.pushMessage(dataMap);
            }
        } catch (IOException e) {
            LOGGER.error(String.format("比对失败[%s]", keyCode), e);
        }
    }

    /**
     * 比对IP信息
     *
     * @param dataMap
     */
    public void analyzeIp(Map dataMap) {
        String keyCode = (String) dataMap.get("keyCode");
        String fileId = (String) dataMap.get("file_id");
        try {
            int total = 0;
            total += this.query("ip_list", keyCode, fileId, "qqloginip");
            total += this.query("ip", keyCode, fileId, "wxloginip");
            total += this.query("ip", keyCode, fileId, "zfblogininfo");
            total += this.query("ip", keyCode, fileId, "xndw_sx");
            total += this.query("ip", keyCode, fileId, "xndw_wsk");
            total += this.query("ip", keyCode, fileId, "yhzh_jyls");
            total += this.query("ip", keyCode, fileId, "email_ip");
            if (total > 0) {
                dataMap.put("total", total);
                dataMap.put("tips", String.format("根据[%s]比中IP信息%s条", keyCode, total));
                messagingManager.pushMessage(dataMap);
            }
        } catch (IOException e) {
            LOGGER.error(String.format("比对失败[%s]", keyCode), e);
        }
    }

    /**
     * 比中email
     *
     * @param dataMap
     */
    public void analyzeEmail(Map dataMap) {
        String keyCode = (String) dataMap.get("keyCode");
        String fileId = (String) dataMap.get("file_id");
        try {
            int total = 0;
            total += this.query("email", keyCode, fileId, "qqreginfo");
            total += this.query("email", keyCode, fileId, "wxreginfo");
            total += this.query("email", keyCode, fileId, "wxlxr");
            total += this.query("from_address", keyCode, fileId, "email");
            total += this.query("to_address", keyCode, fileId, "email");
            total += this.query("email", keyCode, fileId, "zfbreginfo");
            total += this.query("email", keyCode, fileId, "xndw_sx");
            total += this.query("email", keyCode, fileId, "xndw_wsk");
            total += this.query("email", keyCode, fileId, "yhzh_khxx");
            total += this.query("email", keyCode, fileId, "email_reg");
            if (total > 0) {
                dataMap.put("total", total);
                dataMap.put("tips", String.format("根据[%s]比中电子邮箱信息%s条", keyCode, total));
                messagingManager.pushMessage(dataMap);
            }
        } catch (IOException e) {
            LOGGER.error(String.format("比对失败[%s]", keyCode), e);
        }
    }

    private int query(String field, String keyCode, String fileId, String indexName) throws IOException {
        Map map = elasticsearchRestClient.query(String.format("{\"from\":0,\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"should\":[{\"term\":{\"%s\":\"%s\"}}]}}]}},{\"bool\":{\"must_not\":[{\"bool\":{\"should\":[{\"term\":{\"file_id\":\"%s\"}}]}}]}}]}},\"size\":10,\"sort\":[{\"create_time\":{\"order\":\"desc\"}}]}", field, keyCode, fileId), indexName);
        if (map != null) {
            Object total = map.get("total");
            if (total != null && (Integer) total > 0) {
                return (Integer) total;
            }
        }
        return 0;
    }
}
