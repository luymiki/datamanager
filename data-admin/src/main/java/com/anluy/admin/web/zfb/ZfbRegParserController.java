package com.anluy.admin.web.zfb;

import com.alibaba.fastjson.JSON;
import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.*;
import com.anluy.admin.service.AnalyzeCodeAndPushMessage;
import com.anluy.admin.service.AttachmentService;
import com.anluy.admin.utils.IPAddrUtil;
import com.anluy.admin.web.zfb.parser.ZfbJyjlParser;
import com.anluy.admin.web.zfb.parser.ZfbLoginParser;
import com.anluy.admin.web.zfb.parser.ZfbRegParser;
import com.anluy.admin.web.zfb.parser.ZfbZhParser;
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
 * 功能说明：支付宝文件操作
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/zfb")
@Api(value = "/api/admin/zfb", description = "支付宝文件操作")
public class ZfbRegParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZfbRegParserController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private FileManagerConfig fileManagerConfig;
    @Resource
    private IPAddrUtil ipAddrUtil;
    @Resource
    private AnalyzeCodeAndPushMessage analyzeCodeAndPushMessage;

    /**
     * 支付宝文件保存
     *
     * @return
     */
    @ApiOperation(value = "支付宝文件保存", response = Result.class)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Object save(HttpServletRequest request, @RequestBody Attachment attachment) {
        try {
            if (attachment == null) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001, "文件为空"));
            }
            if (StringUtils.isBlank(attachment.getId())) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001, "文件id为空"));
            }
            if (StringUtils.isBlank(attachment.getPath())) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001, "文件路径为空"));
            }
            if (StringUtils.isBlank(attachment.getSuffix())) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001, "文件类型为空"));
            }
            String uploadDir = fileManagerConfig.getUploadDir();
            String path = uploadDir + attachment.getPath();
            List<ZfbRegInfo> zfbRegInfoList = null;
            ZfbRegParser parser = new ZfbRegParser(attachment);
            if (path.toLowerCase().endsWith(".xls") || path.toLowerCase().endsWith(".xlsx")) {
                zfbRegInfoList = parser.parserExcel(path);
                ZfbRegInfo zfbRegInfo = zfbRegInfoList.get(0);
                String xcbh = UUID.randomUUID().toString();
                zfbRegInfo.setXcbh(xcbh);
                ZfbLoginParser zfbLoginParser = new ZfbLoginParser(attachment, ipAddrUtil);
                List<ZfbLoginInfo> zfbLoginInfoList = zfbLoginParser.parserExcel(path, zfbRegInfo.getUserId(), zfbRegInfo.getName(), zfbRegInfo.getName(), xcbh);
                List<Map> zfbLoginsaveList = new ArrayList<>();
                zfbLoginInfoList.forEach(regInfo -> {
                    regInfo.setId(UUID.randomUUID().toString());
                    regInfo.setCreateTime(new Date());
                    Map<String, Object> jsonMap = (Map<String, Object>) JSON.toJSON(regInfo);
                    jsonMap.put("_id", regInfo.getId());
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
                    zfbLoginsaveList.add(jsonMap);
                });

                ZfbJyjlParser zfbJyjlParser = new ZfbJyjlParser(attachment);
                List<ZfbJyjlInfo> zfbJyjlInfoList = zfbJyjlParser.parserExcel(path, zfbRegInfo.getUserId(), zfbRegInfo.getName(), zfbRegInfo.getName(), xcbh);
                List<Map> zfbJyjsaveList = new ArrayList<>();
                zfbJyjlInfoList.forEach(regInfo -> {
                    regInfo.setId(UUID.randomUUID().toString());
                    regInfo.setCreateTime(new Date());
                    regInfo.setName(regInfo.getMjxx());
                    Map<String, Object> jsonMap = (Map<String, Object>) JSON.toJSON(regInfo);
                    jsonMap.put("_id", regInfo.getId());
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
                    zfbJyjsaveList.add(jsonMap);
                });
                ZfbZhParser zfbZhParser = new ZfbZhParser(attachment);
                List<ZfbZhInfo> zfbZhInfoList = zfbZhParser.parserExcel(path, zfbRegInfo.getUserId(), zfbRegInfo.getName(), zfbRegInfo.getName(), xcbh);
                List<Map> zfbZhsaveList = new ArrayList<>();
                zfbZhInfoList.forEach(regInfo->{
                    regInfo.setId(UUID.randomUUID().toString() );
                    regInfo.setCreateTime(new Date());
                    Map<String, Object> jsonMap = (Map<String, Object>)JSON.toJSON(regInfo);
                    jsonMap.put("_id",regInfo.getId());
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
                    zfbZhsaveList.add(jsonMap);
                });



                elasticsearchRestClient.batchSave(zfbLoginsaveList, "zfblogininfo");
                elasticsearchRestClient.batchSave(zfbJyjsaveList, "zfbjyjlinfo");
                elasticsearchRestClient.batchSave(zfbZhsaveList,"zfbzhinfo");
            } else {
                zfbRegInfoList = parser.parser(path);
            }
            List<Map> saveList = new ArrayList<>();
            zfbRegInfoList.forEach(regInfo -> {
                regInfo.setId(UUID.randomUUID().toString());
                regInfo.setCreateTime(new Date());
                Map<String, Object> jsonMap = (Map<String, Object>) JSON.toJSON(regInfo);
                jsonMap.put("_id", regInfo.getId());
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
                saveList.add(jsonMap);
            });

            elasticsearchRestClient.batchSave(saveList, "zfbreginfo");
            analyzeCodeAndPushMessage.analyze(saveList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_PHONE, "dlsj");
            analyzeCodeAndPushMessage.analyze(saveList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_PHONE, "bdsj");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(saveList).setPath(request.getRequestURI()));

        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
        }
    }

    /**
     * 支付宝文件删除
     *
     * @return
     */
    @ApiOperation(value = "支付宝文件删除", response = Result.class)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Object delete(HttpServletRequest request, String id, String fileId) {
        try {
            if (StringUtils.isBlank(fileId)) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "注册文件id为空"));
            }
            if (StringUtils.isBlank(id)) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "注册id为空"));
            }
            Attachment attachment = attachmentService.get(fileId);
            if (attachment != null) {
                String uploadDir = fileManagerConfig.getUploadDir();
                String path = uploadDir + "/" + attachment.getPath();
                File f = new File(path);
                if (f.isFile()) {
                    f.delete();
                }
                attachmentService.delete(fileId);
            }
            Map map = elasticsearchRestClient.get("zfbreginfo", id, null, null);
            String deleteDsl = "{\"query\": { \"match\": {\"xcbh\": \"" + map.get("xcbh") + "\"}}}";
            elasticsearchRestClient.deleteByQuery(deleteDsl, "zfblogininfo");
            elasticsearchRestClient.deleteByQuery(deleteDsl, "zfbzhinfo");
            elasticsearchRestClient.deleteByQuery(deleteDsl, "zfbtxinfo");
            elasticsearchRestClient.deleteByQuery(deleteDsl, "zfbzzinfo");
            elasticsearchRestClient.deleteByQuery(deleteDsl, "zfbjyjlinfo");
            elasticsearchRestClient.remove(id, "zfbreginfo");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("删除成功").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("删除失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
        }
    }
}