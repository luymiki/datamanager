package com.anluy.admin.web;

import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.web.Result;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/")
@Api(value = "/", description = "首页")
public class IndexController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    /**
     * 首页接口
     *
     * @return
     */
    @ApiOperation(value = "首页", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "查询失败")})//错误码说明
    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public Object list(HttpServletRequest request) {
        try {
//            Map datamap = elasticsearchRestClient.query("{\"size\":1000}", "xndw_sx");
//            List<Map> list = (List<Map>)datamap.get("data");
//            List<Map> updataList = new ArrayList<>();
//            list.forEach(map -> {
//                Map m = new HashMap();
//                m.put("_id",map.get("_id"));
//                m.put("create_time",map.get("created_at"));
//                updataList.add(m);
//            });
//            elasticsearchRestClient.batchUpdate(updataList,"xndw_sx");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("查询成功").setData("").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("查询失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
}