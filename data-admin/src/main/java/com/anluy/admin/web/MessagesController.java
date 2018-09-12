package com.anluy.admin.web;

import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/9/2.
 */
@RestController
@RequestMapping("/api/admin/messages")
@Api(value = "/api/admin/messages", description = "消息管理")
public class MessagesController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagesController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    /**
     * 设置消息已读状态
     *
     * @return
     */
    @ApiOperation(value = "设置消息已读状态", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "设置失败")})//错误码说明
    @RequestMapping(value = "/read", method = {RequestMethod.GET, RequestMethod.POST})
    public Object read(HttpServletRequest request,String messageId) {
        try {
            if(StringUtils.isBlank(messageId)){
                LOGGER.error("设置失败:id为空");
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),"设置失败:id为空"));
            }
            Map messageMap = elasticsearchRestClient.get("message",messageId,null,null);
            if("0".equals(messageMap.get("read"))){
                messageMap.put("read","1");
                elasticsearchRestClient.update(messageMap,messageId,"message");
            }

            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("设置成功").setData("").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("设置失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

    private static final String dsl = "{\"from\":0,\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"should\":[{\"term\":{\"read\":\"0\"}}]}}]}}]}},\"size\":1,\"sort\":[{\"create_time\":{\"order\":\"desc\"}}]}";
    /**
     * 设置消息已读状态
     *
     * @return
     */
    @ApiOperation(value = "查询未读消息个数", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "查询未读消息个数失败")})//错误码说明
    @RequestMapping(value = "/count", method = {RequestMethod.GET, RequestMethod.POST})
    public Object count(HttpServletRequest request) {
        try {
            Map messageMap = elasticsearchRestClient.query(dsl,"message");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("查询成功").setData(messageMap).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("设置失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
}