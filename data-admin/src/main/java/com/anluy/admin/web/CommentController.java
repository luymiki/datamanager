package com.anluy.admin.web;

import com.alibaba.fastjson.JSON;
import com.anluy.admin.entity.Comment;
import com.anluy.admin.entity.Suspicious;
import com.anluy.admin.service.SuspiciousService;
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
 * 功能说明：批注信息管理
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/comment")
@Api(value = "/api/admin/comment", description = "批注信息管理")
public class CommentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    /**
     * 保存批注信息接口
     *
     * @return
     */
    @ApiOperation(value = "保存批注信息", response = Result.class)
    @RequestMapping(value = "/save", method = {RequestMethod.GET, RequestMethod.POST})
    public Object save(HttpServletRequest request, Comment comment) {
        try {
            if (comment == null || StringUtils.isBlank(comment.getIndexName()) || StringUtils.isBlank(comment.getSource()) || StringUtils.isBlank(comment.getComment())) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "保存的数据为空"));
            }
            String[] ids = comment.getSource().split(",");
            List<Map> commentList = new ArrayList<>();
            for (String id : ids) {
                Comment com = new Comment();
                com.setId(UUID.randomUUID().toString());
                com.setCreateTime(new Date());
                com.setSource(id);
                com.setIndexName(comment.getIndexName());
                com.setTags(comment.getTags());
                com.setComment(comment.getComment());
                Map<String, Object> jsonMap = (Map<String, Object>) JSON.toJSON(com);
                jsonMap.put("_id", com.getId());
                jsonMap.forEach((k, v) -> {
                    switch (k) {
                        case "create_time": {
                            if (v != null) {
                                jsonMap.put(k, DateFormatUtils.format((Date) v, "yyyy-MM-dd HH:mm:ss"));
                            }
                            break;
                        }
                        case "tags": {
                            if (v != null) {
                                jsonMap.put(k, ((String) v).split(","));
                            }
                            break;
                        }
                    }
                });
                commentList.add(jsonMap);
            }
            //新增
            elasticsearchRestClient.batchSave(commentList,"comment");

            //suspiciousService.save(comment);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(comment).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
        }
    }

    /**
     * 删除批注信息接口
     *
     * @return
     */
    @ApiOperation(value = "删除批注信息", response = Result.class)
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public Object delete(HttpServletRequest request, Comment comment) {
        try {
            if (comment == null || StringUtils.isBlank(comment.getId())) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "待删除的数据ID为空"));
            }
            elasticsearchRestClient.remove(comment.getId(),"comment");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("删除成功").setData(comment).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("删除失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
        }
    }
//
//    /**
//     * 分析可疑人信息接口
//     *
//     * @return
//     */
//    @ApiOperation(value = "分析可疑人信息", response = Result.class)
//    @RequestMapping(value = "/analyze", method = {RequestMethod.GET, RequestMethod.POST})
//    public Object analyze(HttpServletRequest request, Suspicious suspicious) {
//        try {
//            if (suspicious == null || StringUtils.isBlank(suspicious.getId())) {
//                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "待提取的数据ID为空"));
//            }
//            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
//            suspiciousService.analyze(suspicious.getId(), token);
//            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("提取成功").setData(suspicious).setPath(request.getRequestURI()));
//        } catch (Exception exception) {
//            LOGGER.error("提取失败:" + exception.getMessage(), exception);
//            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
//        }
//    }

}