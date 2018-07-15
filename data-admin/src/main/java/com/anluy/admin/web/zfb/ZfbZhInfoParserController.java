package com.anluy.admin.web.zfb;

import com.alibaba.fastjson.JSON;
import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.ZfbLoginInfo;
import com.anluy.admin.entity.ZfbZhInfo;
import com.anluy.admin.service.AttachmentService;
import com.anluy.admin.web.zfb.parser.ZfbLoginParser;
import com.anluy.admin.web.zfb.parser.ZfbZhParser;
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
import java.io.File;
import java.util.*;

/**
 * 功能说明：支付宝账户明细文件操作
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/zfb/zhinfo")
@Api(value = "/api/admin/zfb/zhinfo", description = "支付宝账户明细文件操作")
public class ZfbZhInfoParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZfbZhInfoParserController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private FileManagerConfig fileManagerConfig;


    /**
     * 支付宝账户明细文件保存
     *
     * @return
     */
    @ApiOperation(value = "支付宝账户明细文件保存", response = Result.class)
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
            ZfbZhParser parser = new ZfbZhParser(attachment);
            List<ZfbZhInfo> zfbZhInfoList = parser.parser(path);
            List<Map> saveList = new ArrayList<>();
            zfbZhInfoList.forEach(regInfo->{
                regInfo.setId(UUID.randomUUID().toString() );
                regInfo.setCreateTime(new Date());
                Map<String, Object> jsonMap = (Map<String, Object>)JSON.toJSON(regInfo);
                jsonMap.put("_id",regInfo.getId());
                jsonMap.forEach((k,v)->{
                    switch (k){
                        case "jysj":
                        case "jycjsj":
                        case "fksj":
                        case "zjxgsj":
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
                saveList.add(jsonMap);
            });

            elasticsearchRestClient.batchSave(saveList,"zfbzhinfo");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(saveList).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

//    /**
//     * 支付宝账户明细文件删除
//     *
//     * @return
//     */
//    @ApiOperation(value = "支付宝账户明细文件删除", response = Result.class)
//    @RequestMapping(value = "/delete",method =RequestMethod.POST)
//    public Object delete(HttpServletRequest request,String id,String fileId) {
//        try {
//            if(StringUtils.isBlank(fileId)){
//                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"注册文件id为空"));
//            }
//            if(StringUtils.isBlank(id)){
//                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"注册id为空"));
//            }
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
//
//            String deleteDsl = "{\"query\": { \"match\": {\"zfb_id\": \""+id+"\"}}}";
//            elasticsearchRestClient.deleteByQuery(deleteDsl,"zfbtrades");
//            elasticsearchRestClient.remove(id,"zfbreginfo");
//            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("删除成功").setPath(request.getRequestURI()));
//        } catch (Exception exception) {
//            LOGGER.error("删除失败:" + exception.getMessage(), exception);
//            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
//        }
//    }
}