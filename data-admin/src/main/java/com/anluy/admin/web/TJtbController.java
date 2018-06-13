package com.anluy.admin.web;

import com.alibaba.fastjson.JSON;
import com.anluy.admin.entity.Comment;
import com.anluy.admin.service.TjtbService;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 功能说明：统计图表功能
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/tjtb")
@Api(value = "/api/admin/tjtb", description = "统计图表功能")
public class TjtbController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TjtbController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private TjtbService tjtbService;


    /**
     * 分析可疑人信息接口
     *
     * @return
     */
    @ApiOperation(value = "分析系统数据创建时间信息", response = Result.class)
    @RequestMapping(value = "cjsj", method = {RequestMethod.GET, RequestMethod.POST})
    public Object analyzeCjsj(HttpServletRequest request) {
        try {
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjtbService.analyzeCjsj(token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("分析完成").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("分析失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
        }
    }

}