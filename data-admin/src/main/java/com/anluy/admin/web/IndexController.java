package com.anluy.admin.web;

import com.anluy.admin.utils.IPAddrUtil;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.web.Result;
import io.swagger.annotations.*;
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
import java.io.IOException;
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
    @Resource
    private IPAddrUtil ipAddrUtil;
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
    /**
     * 首页接口
     *
     * @return
     */
    @ApiOperation(value = "设置ip", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "查询失败")})//错误码说明
    @RequestMapping(value = "/setip", method = {RequestMethod.GET, RequestMethod.POST})
    public Object setIP(HttpServletRequest request) {
        try {
            setIp("qqloginip_list","ip");
            setIp("wxloginip","ip");
            setIp("zfblogininfo","ip");
            setIp("email_ip","ip");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("设置完成").setData("").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("查询失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    private void setIp(String indexName,String ipField) throws IOException {
        elasticsearchRestClient.scroll("{\"size\":1000}",null,new ElasticsearchRestClient.TimeWindowCallBack(){
            @Override
            public void process(List<Map> var1) {
                List<Map> listmap = new ArrayList<>();
                var1.forEach(map->{
                    String ip = (String) map.get(ipField);
                    if(StringUtils.isNotBlank(ip)){
                        String gsd = ipAddrUtil.findCityInfoString(ip);
                        map.put("gsd",gsd);
                        map.put("_id",map.get("id"));
                        listmap.add(map);
                    }
                });
                if(!listmap.isEmpty()){
                    elasticsearchRestClient.batchUpdate(listmap,indexName);
                }
            }
        },indexName,null,null);
    }

    /**
     * 设置人员类型
     *
     * @return
     */
    @ApiOperation(value = "设置人员类型", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "设置人员类型失败")})//错误码说明
    @RequestMapping(value = "/setsusptype", method = {RequestMethod.GET, RequestMethod.POST})
    public Object setSuspType(HttpServletRequest request) {
        try {
            elasticsearchRestClient.scroll("{\"size\":1000}",null,new ElasticsearchRestClient.TimeWindowCallBack(){
                @Override
                public void process(List<Map> var1) {
                    List<Map> listmap = new ArrayList<>();
                    var1.forEach(map->{
                        String type = (String) map.get("type");
                        if("2".equals(type)){
                            map.put("type","关系人");
                        }else{
                            map.put("type","可疑人");
                        }
                        map.put("_id",map.get("id"));
                        listmap.add(map);
                    });
                    if(!listmap.isEmpty()){
                        elasticsearchRestClient.batchUpdate(listmap,"suspicious");
                    }
                }
            },"suspicious",null,null);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("设置完成").setData("").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("查询失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
}