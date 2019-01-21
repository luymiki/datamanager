package com.anluy.admin.web.huadan;

import com.alibaba.fastjson.JSON;
import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.CftRegInfo;
import com.anluy.admin.entity.HuadanInfo;
import com.anluy.admin.entity.HuadanList;
import com.anluy.admin.service.AnalyzeCodeAndPushMessage;
import com.anluy.admin.service.AttachmentService;
import com.anluy.admin.web.cft.parser.CftRegParser;
import com.anluy.admin.web.huadan.parser.HuadanParser;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * 功能说明：话单文件操作
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/huadan")
@Api(value = "/api/admin/huadan", description = "话单文件操作")
public class HuadanParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HuadanParserController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private FileManagerConfig fileManagerConfig;
    @Resource
    private AnalyzeCodeAndPushMessage analyzeCodeAndPushMessage;
    /**
     * 话单文件保存
     *
     * @return
     */
    @ApiOperation(value = "话单文件保存", response = Result.class)
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
            if(StringUtils.isBlank(attachment.getFolder())){//借用这个字段来放运营商名称
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"运营商为空"));
            }
            String uploadDir = fileManagerConfig.getUploadDir();
            String path = uploadDir + attachment.getPath();
            HuadanParser parser = new HuadanParser(attachment.getId());
            HuadanInfo regInfo = parser.parser(attachment,path);
            regInfo.setSuspId(attachment.getSuspId());
            regInfo.setSuspName(attachment.getSuspName());
            regInfo.setTags(attachment.getTags());
            regInfo.setId(UUID.randomUUID().toString() );
            regInfo.setCreateTime(new Date());

            List<HuadanList> huadanLists = regInfo.getHuadanLists();
            regInfo.setHuadanLists(null);

            Map<String, Object> jsonMap = (Map<String, Object>)JSON.toJSON(regInfo);
            jsonMap.forEach((k,v)->{
                switch (k){
                    case "tags":{
                        if(v!=null){
                            jsonMap.put(k,((String)v).split(","));
                        }
                        break;
                    }
                }
            });
            jsonMap.remove("huadanLists");

            List<Map> hdSavelist = new ArrayList<>();
            huadanLists.forEach(huadanList->{
                huadanList.setId(UUID.randomUUID().toString());
                huadanList.setHdId(regInfo.getId());
                huadanList.setCreateTime(new Date());
                Map<String, Object> map = (Map<String, Object>)JSON.toJSON(huadanList);
                map.forEach((k,v)->{
                    switch (k){
                        case "tags":{
                            if(v!=null){
                                map.put(k,((String)v).split(","));
                            }
                            break;
                        }
                    }
                });
                map.put("_id",huadanList.getId());
                hdSavelist.add(map);
            });

            elasticsearchRestClient.batchSave(hdSavelist,"huaduan_list");
            elasticsearchRestClient.save(jsonMap,regInfo.getId(),"huaduan");
            analyzeCodeAndPushMessage.analyze(hdSavelist, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_PHONE,"ddhm");
            analyzeCodeAndPushMessage.analyze(jsonMap, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_PHONE,"zjhm");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(regInfo).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

    /**
     * 话单文件删除
     *
     * @return
     */
    @ApiOperation(value = "话单文件删除", response = Result.class)
    @RequestMapping(value = "/delete",method =RequestMethod.POST)
    public Object delete(HttpServletRequest request,String id,String fileId) {
        try {
            if(StringUtils.isBlank(fileId)){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"注册文件id为空"));
            }
            if(StringUtils.isBlank(id)){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"注册id为空"));
            }
            Attachment attachment = attachmentService.get(fileId);
            if(attachment!=null){
                String uploadDir = fileManagerConfig.getUploadDir();
                String path = uploadDir +"/"+attachment.getPath();
                File f = new File(path);
                if(f.isFile()){
                    f.delete();
                }
                attachmentService.delete(fileId);
            }
            String deleteDsl = "{\"query\": { \"match\": {\"hd_id\": \""+id+"\"}}}";
            elasticsearchRestClient.deleteByQuery(deleteDsl,"huaduan_list");
            elasticsearchRestClient.remove(id,"huaduan");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("删除成功").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("删除失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
}