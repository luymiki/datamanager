package com.anluy.admin.eqa.web;

import com.anluy.admin.eqa.core.ElasticsearchQueryAnalyzeEngine;
import com.anluy.commons.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 功能说明：全文检索查询接口
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/eqa")
@Api(value = "/api/eqa", description = "全文检索接口")
public class FullTextQueryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FullTextQueryController.class);
    @Resource
    private ElasticsearchQueryAnalyzeEngine elasticsearchQueryAnalyzeEngine;
    @Value("${fulltext.index}")
    private String indexNames;

    /**
     * 全文检索接口
     *
     * @return
     */
    @ApiOperation(value = "全文检索接口", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "查询失败"),
            @ApiResponse(code = 501, message = "查询参数异常")})//错误码说明
    @RequestMapping(value = "/fulltext",method = {RequestMethod.GET,RequestMethod.POST})
    public Object fulltext(HttpServletRequest request,Integer pageNum,Integer pageSize,String keyword,String indexName,String sort) {
        try {
            if(pageSize==null){
                pageSize = 10;
            }
            if(pageNum==null){
                pageNum = 1;
            }
            if(StringUtils.isBlank(keyword)){
                LOGGER.error("查询失败，查询条件为空");
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(500,"查询失败，查询条件为空"));
            }
            if(StringUtils.isBlank(indexName)){
                indexName = indexNames;
            }
            Map result = elasticsearchQueryAnalyzeEngine.fulltext(keyword,pageNum,pageSize,indexName,sort);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("查询成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("查询失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(500,"查询失败").setData(exception.getMessage()));
        }
    }
}