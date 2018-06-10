package com.anluy.admin.web.xndw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.XndwWskInfo;
import com.anluy.admin.service.AlUserLocInfoService;
import com.anluy.admin.service.AttachmentService;
import com.anluy.admin.web.xndw.parser.XndwWskParser;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 功能说明：WSK虚拟定位文件操作
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/xndw/wsk")
@Api(value = "/api/admin/xndw/wsk", description = "WSK虚拟定位文件操作")
public class XndwWskParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(XndwWskParserController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private FileManagerConfig fileManagerConfig;

    /**
     * WSK虚拟定位文件保存,先备份和删除老的数据，然后新增
     *
     * @return
     */
    @ApiOperation(value = "WSK虚拟定位文件保存", response = Result.class)
    @RequestMapping(value = "/save",method =RequestMethod.POST)
    public Object save(HttpServletRequest request,  @RequestBody  Attachment attachment) {
        try {
            if(attachment == null  ){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"文件为空"));
            }
            if(StringUtils.isBlank(attachment.getId())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"文件id为空"));
            }
            if(StringUtils.isBlank(attachment.getPath())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"文件路径为空"));
            }
            if(StringUtils.isBlank(attachment.getSuffix())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"文件类型为空"));
            }

            String uploadDir = fileManagerConfig.getUploadDir();
            String path = uploadDir + attachment.getPath();
            XndwWskParser parser = new XndwWskParser(attachment.getId());
            List<XndwWskInfo> dataList =  parser.parser(attachment,path);
            List<Map> saveList = new ArrayList<>();
            dataList.forEach(xndwWskInfo -> {
                Map<String, Object> jsonMap = (Map<String, Object>)JSON.toJSON(xndwWskInfo);
                jsonMap.forEach((k,v)->{
                    switch (k){
                        case "create_time":{
                            if(v!=null){
                                jsonMap.put(k, DateFormatUtils.format((Date)v,"yyyy-MM-dd HH:mm:ss"));
                            }
                            break;
                        }
                        case "tags":{
                            if(v!=null){
                                jsonMap.put(k,((String)v).split(","));
                            }
                            break;
                        }
                    }
                });
               jsonMap.put("_id",xndwWskInfo.getId());
                saveList.add(jsonMap);
            });
            //先删除
            String deleteDsl = "{\"query\": { \"match_all\": {}}}";
            elasticsearchRestClient.deleteByQuery(deleteDsl,"xndw_wsk");
            //保存数据到es
            elasticsearchRestClient.batchSave(saveList,"xndw_wsk");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
}