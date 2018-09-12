package com.anluy.admin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 功能说明：推送和保存消息
 * <p>
 * Created by hc.zeng on 2018/8/28.
 */
@Component
public class MessagingManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingManager.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    /**
     * 推送消息
     *
     * @param message
     * @return
     */
    public void pushMessage(String message) {
        this.pushMessage((JSONObject)JSON.toJSON(message));

    }

    /**
     * 推送消息
     *
     * @param messageMap
     * @return
     */
    public void pushMessage(Map messageMap) {
        try {
            Map map = new HashMap();
            String id = UUID.randomUUID().toString();
            map.put("id", id);
            map.put("create_time", DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
            map.put("tips", messageMap.get("tips"));
            map.put("read", "0");
            String message = JSON.toJSONString(map);
            simpMessagingTemplate.convertAndSendToUser("admin", "/msg", message);

            map.put("message", JSON.toJSONString(messageMap));
            this.saveMessage(map);
        } catch (Exception e) {
            LOGGER.error("推送失败", e);
        }
    }

    private void saveMessage(Map messageMap){
        Map map = new HashMap();
        String id = UUID.randomUUID().toString();
        map.put("id", id);
        map.put("create_time", DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        map.put("message", JSON.toJSONString(messageMap));
        map.put("tips", messageMap.get("tips"));
        map.put("read", "0");
        elasticsearchRestClient.save(map,id,"message");
    }






}
