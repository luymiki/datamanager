package com.anluy.admin.web.zfb;

import com.alibaba.fastjson.JSON;
import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.ZfbJyjlInfo;
import com.anluy.admin.entity.ZfbZzInfo;
import com.anluy.admin.service.AttachmentService;
import com.anluy.admin.web.zfb.parser.ZfbJyjlParser;
import com.anluy.admin.web.zfb.parser.ZfbZhParser;
import com.anluy.admin.web.zfb.parser.ZfbZzParser;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 功能说明：支付宝转账明细文件操作
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/zfb/zzinfo")
@Api(value = "/api/admin/zfb/zzinfo", description = "支付宝转账明细文件操作")
public class ZfbZzInfoParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZfbZzInfoParserController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private FileManagerConfig fileManagerConfig;


    /**
     * 支付宝转账明细文件保存
     *
     * @return
     */
    @ApiOperation(value = "支付宝转账明细文件保存", response = Result.class)
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
            ZfbZzParser parser = new ZfbZzParser(attachment);
            Set<String> xcbhSet = new HashSet<>();
            List<ZfbZzInfo> zfbJyjlInfoList = parser.parser(path,xcbhSet);
            Map<String,String> mapping = new HashMap<>();//xcbh与userid的映射关系
            //xcbhSet
            if(!xcbhSet.isEmpty()){
                StringBuffer stringBuffer = new StringBuffer();
                AtomicInteger ai = new AtomicInteger(0);
                xcbhSet.forEach(xcbh->{
                    if(ai.get()>0){
                        stringBuffer.append(",");
                    }
                    stringBuffer.append("\"").append(xcbh).append("\"");
                    ai.getAndAdd(1);
                });
                String dsl = "{\"from\": 0,\"query\": {\"bool\": {\"must\": {\"terms\": {\"xcbh\": ["+stringBuffer.toString()+"]}}}},\"size\": 1000}";
                Map rm = elasticsearchRestClient.query(dsl,"zfbreginfo");
                if(rm!=null && !rm.isEmpty()){
                    List<Map> lm = (List<Map> ) rm.get("data");
                    if(lm!=null){
                        lm.forEach(map->{
                            if(!mapping.containsKey(map.get("user_id"))){
                                mapping.put((String)map.get("user_id"),(String) map.get("xcbh"));
                            }
                        });
                    }
                }
            }

            List<Map> saveList = new ArrayList<>();
            zfbJyjlInfoList.forEach(regInfo->{
                regInfo.setId(UUID.randomUUID().toString() );
                regInfo.setCreateTime(new Date());

                if(!mapping.containsKey( regInfo.getSkfId()) && !mapping.containsKey( regInfo.getFkfId())){
                    return;
                }
                String userId = null;
                String xcbh = mapping.get( regInfo.getSkfId());
                if(StringUtils.isNotBlank(xcbh) && regInfo.getXcbh().equals(xcbh)){
                    userId = regInfo.getSkfId();
                }
                if(userId == null){
                    xcbh = mapping.get( regInfo.getFkfId());
                    if(StringUtils.isNotBlank(xcbh) && regInfo.getXcbh().equals(xcbh)){
                        userId = regInfo.getFkfId();
                    }
                }
                if(userId == null){
                    return;
                }
                regInfo.setUserId(userId);
                if("提现".equals(regInfo.getZzcpmc())){
                    regInfo.setJdlx("出");
                    regInfo.setDsId(regInfo.getSkfId());
                }else {
                    //如果是付款方账户id等于支付宝id说明是转出
                    if (regInfo.getSkfId() != null && regInfo.getSkfId().equals(userId)) {
                        regInfo.setJdlx("入");
                        regInfo.setDsId(regInfo.getFkfId());
                    } else {
                        regInfo.setJdlx("出");
                        regInfo.setDsId(regInfo.getSkfId());
                    }
                }

                Map<String, Object> jsonMap = (Map<String, Object>)JSON.toJSON(regInfo);
                jsonMap.put("_id",regInfo.getId());
                jsonMap.forEach((k,v)->{
                    switch (k){
                        case "jysj":
                        case "dzsj":
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
            if(!saveList.isEmpty()){
                elasticsearchRestClient.batchSave(saveList,"zfbzzinfo");
            }

            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(saveList).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

//    /**
//     * 支付宝转账明细文件删除
//     *
//     * @return
//     */
//    @ApiOperation(value = "支付宝转账明细文件删除", response = Result.class)
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