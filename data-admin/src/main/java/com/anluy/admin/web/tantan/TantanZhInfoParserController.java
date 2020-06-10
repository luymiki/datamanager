package com.anluy.admin.web.tantan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.*;
import com.anluy.admin.service.AttachmentService;
import com.anluy.admin.web.tantan.parser.TantanHyxxParser;
import com.anluy.admin.web.tantan.parser.TantanLtjlParser;
import com.anluy.admin.web.tantan.parser.TantanZhParser;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
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
import java.util.*;

/**
 * 功能说明：探探账户明细文件操作
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/tantan/zhinfo")
@Api(value = "/api/admin/tantan/zhinfo", description = "探探账户明细文件操作")
public class TantanZhInfoParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TantanZhInfoParserController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private FileManagerConfig fileManagerConfig;


    /**
     * 探探账户明细文件保存
     *
     * @return
     */
    @ApiOperation(value = "探探账户明细文件保存", response = Result.class)
    @RequestMapping(value = "/save",method =RequestMethod.POST)
    public Object save(HttpServletRequest request, @RequestBody  Attachment attachment) {
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
            TantanZhParser parser = new TantanZhParser(attachment);
            TantanHyxxParser hyxxParser = new TantanHyxxParser(attachment);
            TantanLtjlParser ltjlParser = new TantanLtjlParser(attachment);
            TantanZhInfo tantanZhInfo = parser.parser(path,fileManagerConfig);
            List<TantanHyxxInfo> tantanHyxxInfoList = hyxxParser.parser(path,fileManagerConfig);
            List<TantanLtjlInfo> tantanLtjlInfoList = ltjlParser.parser(path,fileManagerConfig);
            List<Map> saveList = new ArrayList<>();
            tantanZhInfo.setId(UUID.randomUUID().toString() );
            tantanZhInfo.setCreateTime(new Date());
            Map<String, Object> jsonMap = (Map<String, Object>) JSON.toJSON(tantanZhInfo);
            jsonMap.forEach((k, v) -> {
                switch (k) {
                    case "tags": {
                        if (v != null) {
                            jsonMap.put(k, ((String) v).split(","));
                        }
                        break;
                    }
                }
            });
            elasticsearchRestClient.save(jsonMap,tantanZhInfo.getId(),"tantanzhinfo");

            List<Map> hyList = new ArrayList<>();
            tantanHyxxInfoList.forEach(tantanHyxxInfo -> {
                tantanHyxxInfo.setId(UUID.randomUUID().toString() );
                tantanHyxxInfo.setZhid(tantanZhInfo.getId());
                tantanHyxxInfo.setCreateTime(new Date());
                Map<String, Object> jm = (Map<String, Object>) JSON.toJSON(tantanHyxxInfo);
                jm.put("_id",tantanHyxxInfo.getId());
                hyList.add(jm);
            });
            elasticsearchRestClient.batchSave(hyList,"tantanhyxxinfo");

            List<Map> ltjlList = new ArrayList<>();
            tantanLtjlInfoList.forEach(tantanLtjlInfo -> {
                tantanLtjlInfo.setId(UUID.randomUUID().toString() );
                tantanLtjlInfo.setZhid(tantanZhInfo.getId());
                tantanLtjlInfo.setCreateTime(new Date());
                Map<String, Object> jm = (Map<String, Object>) JSON.toJSON(tantanLtjlInfo);
                jm.put("_id",tantanLtjlInfo.getId());
                ltjlList.add(jm);
            });
            elasticsearchRestClient.batchSave(ltjlList,"tantanltjlinfo");

            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(saveList).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

    /**
     * 探探账户明细文件删除
     *
     * @return
     */
    @ApiOperation(value = "探探账户明细文件删除", response = Result.class)
    @RequestMapping(value = "/delete",method =RequestMethod.POST)
    public Object delete(HttpServletRequest request,String id,String fileId) {
        try {
            if(StringUtils.isBlank(fileId)){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"注册文件id为空"));
            }
            if(StringUtils.isBlank(id)){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"注册id为空"));
            }
//            Attachment attachment = attachmentService.get(fileId);
//            if(attachment!=null){
//                String uploadDir = fileManagerConfig.getUploadDir();
//                String path = uploadDir +"/"+attachment.getPath();
//                File f = new File(path);
//                if(f.isFile()){
//                    f.delete();
//                }
//                attachmentService.delete(fileId);
//            }

            String deleteDsl = "{\"query\": { \"match\": {\"tantan_id\": \""+id+"\"}}}";
            elasticsearchRestClient.deleteByQuery(deleteDsl,"tantanzhinfo");
            elasticsearchRestClient.remove(id,"tantanzhinfo");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("删除成功").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("删除失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
}