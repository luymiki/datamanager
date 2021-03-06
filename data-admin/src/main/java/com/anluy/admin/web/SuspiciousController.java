package com.anluy.admin.web;

import com.anluy.admin.entity.Suspicious;
import com.anluy.admin.service.SuspiciousService;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import java.util.*;

/**
 * 功能说明：嫌疑人信息管理
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/suspicious")
@Api(value = "/api/admin/suspicious", description = "嫌疑人信息管理")
public class SuspiciousController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SuspiciousController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private SuspiciousService suspiciousService;

    /**
     * 保存嫌疑人信息接口
     *
     * @return
     */
    @ApiOperation(value = "保存嫌疑人信息", response = Result.class)
    @RequestMapping(value = "/save", method = {RequestMethod.GET, RequestMethod.POST})
    public Object save(HttpServletRequest request, Suspicious suspicious) {
        try {
            if (suspicious == null || StringUtils.isBlank(suspicious.getName()) || StringUtils.isBlank(suspicious.getGmsfzh())) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "保存的数据为空"));
            }
            //新增
            if (StringUtils.isBlank(suspicious.getId())) {
                suspicious.setCreateTime(new Date());
                suspicious.setModifyTime(new Date());
                suspiciousService.save(suspicious);
                return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(suspicious).setPath(request.getRequestURI()));
            } else {//修改
                Suspicious susp = suspiciousService.get(suspicious.getId());
                suspicious.setCreateTime(susp.getCreateTime());
                suspicious.setModifyTime(new Date());
                suspiciousService.update(suspicious);
                return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("修改成功").setData(suspicious).setPath(request.getRequestURI()));
            }
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
        }
    }

    private static String DSL = "{\"query\":{\"bool\":{\"must\":{\"term\":{\"susp_id\":\"%s\"}}}},\"size\":1000}";

    /**
     * 删除嫌疑人信息接口
     *
     * @return
     */
    @ApiOperation(value = "删除嫌疑人信息", response = Result.class)
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public Object delete(HttpServletRequest request, Suspicious suspicious) {
        try {
            if (suspicious == null || StringUtils.isBlank(suspicious.getId())) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "待删除的数据ID为空"));
            }
            String dsl = String.format(DSL, suspicious.getId());
            elasticsearchRestClient.scrollSource(dsl, null, new ElasticsearchRestClient.TimeWindowCallBack() {
                @Override
                public void process(List<Map> mapList) {
                    if (!mapList.isEmpty()) {
                        Map<String, List<String>> mm = new HashMap<>();
                        mapList.forEach(map -> {
                            String _index = (String) map.get("_index");
                            String id = (String) map.get("_id");
                            if (!mm.containsKey(_index)) {
                                mm.put(_index, new ArrayList());
                            }
                            mm.get(_index).add(id);
                        });
                        mm.forEach((key,idList)->elasticsearchRestClient.batchDelete(idList,key));
                    }
                }
            }, "", null, null);
            suspiciousService.delete(suspicious.getId());
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("删除成功").setData(suspicious).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("删除失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
        }
    }

    /**
     * 分析可疑人信息接口
     *
     * @return
     */
    @ApiOperation(value = "分析可疑人信息", response = Result.class)
    @RequestMapping(value = "/analyze", method = {RequestMethod.GET, RequestMethod.POST})
    public Object analyze(HttpServletRequest request, Suspicious suspicious) {
        try {
            if (suspicious == null || StringUtils.isBlank(suspicious.getId())) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "待提取的数据ID为空"));
            }
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            suspiciousService.analyze(suspicious.getId(), token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("提取成功").setData(suspicious).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("提取失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
        }
    }

}