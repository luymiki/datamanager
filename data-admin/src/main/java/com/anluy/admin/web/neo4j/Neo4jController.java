package com.anluy.admin.web.neo4j;

import com.anluy.admin.service.Neo4jService;
import com.anluy.admin.web.AuthorizationController;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2019/2/16.
 */
@RestController
@RequestMapping("/api/admin/neo4j")
@Api(value = "/api/admin/neo4j", description = "neo4j操作")
public class Neo4jController {
    private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jController.class);

    @Resource
    private Neo4jService neo4jService;



    /**
     * 人员信息查询
     *
     * @return
     */
    @ApiOperation(value = "人员信息查询", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "人员信息查询失败")})//错误码说明
    @RequestMapping(value = "/test", method = {RequestMethod.GET, RequestMethod.POST})
    public Object test(HttpServletRequest request, String p, String cypher) {
        try {
            if (StringUtils.isBlank(cypher)) {
                LOGGER.error("人员信息查询失败:类型为空");
                return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息查询失败:类型为空"));
            }
            Map map = "1".equals(p) ? neo4jService.queryCypherPath(cypher) : neo4jService.queryCypher(cypher);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("人员信息查询完成").setData(map).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("人员信息查询失败:" + exception.getMessage(), exception);
            return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息查询失败:" + exception.getMessage()));
        }
    }

    /**
     * 人员信息查询
     *
     * @return
     */
    @ApiOperation(value = "人员信息查询", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "人员信息查询失败")})//错误码说明
    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public Object query(HttpServletRequest request,
                        @RequestParam(required = false) String type,
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String type2,
                        @RequestParam(required = false) String keyword2
    ) {
        try {
            if (StringUtils.isBlank(type)) {
                LOGGER.error("人员信息查询失败:类型为空");
                return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息查询失败:类型为空"));
            }
            if (StringUtils.isBlank(keyword)) {
                LOGGER.error("人员信息查询失败:关键词为空");
                return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息查询失败:关键词为空"));
            }
            String cypher = null;
            Map map = null;
            //关键词2不为空时校验类型2
            if (StringUtils.isNotBlank(keyword2)) {
                if (StringUtils.isBlank(type2)) {
                    LOGGER.error("人员信息查询失败:类型为空");
                    return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息查询失败:类型为空"));
                }
                cypher = neo4jService.createCypherPath(type, keyword, type2, keyword2);
                map = neo4jService.queryCypherPath(cypher);
            } else {
                cypher = neo4jService.createCypher(type, keyword);
                map = neo4jService.queryCypherPath(cypher);
            }
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("人员信息查询完成").setData(map).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("人员信息查询失败:" + exception.getMessage(), exception);
            return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息查询失败:" + exception.getMessage()));
        }
    }


    /**
     * 人员信息入图
     *
     * @return
     */
    @ApiOperation(value = "人员信息入图", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "人员信息入图失败")})//错误码说明
    @RequestMapping(value = "/susp2neo4j", method = {RequestMethod.GET, RequestMethod.POST})
    public Object susp2neo4j(HttpServletRequest request, @RequestParam(required = false) String id) {
        try {
            if (StringUtils.isBlank(id)) {
                LOGGER.error("人员信息入图失败:人员编号为空");
                return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息入图失败:人员编号为空"));
            }
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            neo4jService.createNeo4j(id,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("人员信息入图完成").setData("").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("人员信息入图失败:" + exception.getMessage(), exception);
            return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息入图失败:" + exception.getMessage()));
        }
    }
}