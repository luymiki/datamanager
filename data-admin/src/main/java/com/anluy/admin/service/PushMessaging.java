package com.anluy.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * 功能说明：推送消息给admin用户
 * <p>
 * Created by hc.zeng on 2018/7/2.
 */
@Service
public class PushMessaging {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 推送消息给admin用户
     * @param message
     */
    public void sendMessageToAdmin(String message){
        simpMessagingTemplate.convertAndSendToUser("admin","/msg",message);
    }
}
