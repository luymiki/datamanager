package com.anluy.admin.web;

import com.anluy.commons.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ${DESCRIPTION}
 *
 * @author hc.zeng
 * @create 2018-06-21 15:03
 */
@Controller
@RequestMapping("/api/admin/websocket")
@Api(value = "/api/admin/websocket", description = "websocket接口")
public class WebSocketController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * @return
     */
    @MessageMapping(value = "/public/push")
    @SendTo("/topic/public")
    public Object pushMsg(String message) {
        try {
            //simpMessagingTemplate.convertAndSend("/send",message);
            return message;
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return ResponseEntity.status(HttpStatus.OK).body("系统错误:" + e.getMessage());
        }
    }
    /**
     * @return
     */
    @MessageMapping(value = "/single/push")
    @ApiOperation(value = "推送个人订阅", response = String.class)
    @RequestMapping(value = "/single/push",method = {RequestMethod.POST, RequestMethod.GET})
    public Object pushSingle(@RequestParam String message) {
        try {
            //JSONObject msg = (JSONObject)JSON.parse(message);
            simpMessagingTemplate.convertAndSendToUser("admin","/msg",message);
            return ResponseEntity.status(HttpStatus.OK).body("推送完成！");
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return ResponseEntity.status(HttpStatus.OK).body("系统错误:" + e.getMessage());
        }
    }
}
