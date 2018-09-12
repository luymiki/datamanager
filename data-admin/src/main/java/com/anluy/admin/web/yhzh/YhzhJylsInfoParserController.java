package com.anluy.admin.web.yhzh;

import com.alibaba.fastjson.JSON;
import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.YhzhJylsInfo;
import com.anluy.admin.entity.ZfbJyjlInfo;
import com.anluy.admin.service.AnalyzeCodeAndPushMessage;
import com.anluy.admin.service.AttachmentService;
import com.anluy.admin.utils.MD5;
import com.anluy.admin.web.yhzh.parser.YhzhJylsParser;
import com.anluy.admin.web.zfb.parser.ZfbJyjlParser;
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

/**
 * 功能说明：银行交易流水文件操作
 * <p>
 * Created by hc.zeng on 2018/6/24.
 */
@RestController
@RequestMapping("/api/admin/yhzh/jyls")
@Api(value = "/api/admin/yhzh/jyls", description = "银行交易流水文件操作")
public class YhzhJylsInfoParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(YhzhJylsInfoParserController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private FileManagerConfig fileManagerConfig;

    @Resource
    private AnalyzeCodeAndPushMessage analyzeCodeAndPushMessage;
    /**
     * 银行交易记录文件保存
     *
     * @return
     */
    @ApiOperation(value = "银行交易流水文件保存", response = Result.class)
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
            //借用这个字段放所属银行信息
            if(StringUtils.isBlank(attachment.getFolder())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"所属银行为空"));
            }
            String uploadDir = fileManagerConfig.getUploadDir();
            String path = uploadDir + attachment.getPath();
            YhzhJylsParser parser = new YhzhJylsParser(attachment);
            List<YhzhJylsInfo> zfbJyjlInfoList = parser.parser("",path);
            List<Map> saveList = new ArrayList<>();
            zfbJyjlInfoList.forEach(regInfo->{
                String md5 = MD5.encode(JSON.toJSONString(regInfo));
//                regInfo.setId(UUID.randomUUID().toString() );
                regInfo.setId(md5);//用MD5做主键，去重数据
                regInfo.setCreateTime(new Date());
                regInfo.setSsyh(attachment.getFolder());
                Map<String, Object> jsonMap = (Map<String, Object>)JSON.toJSON(regInfo);
                jsonMap.put("_id",regInfo.getId());
                jsonMap.forEach((k,v)->{
                    switch (k){
                        case "jysj":
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

            elasticsearchRestClient.batchSave(saveList,"yhzh_jyls");
            analyzeCodeAndPushMessage.analyze(saveList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_YHZH,"dfkh");
            analyzeCodeAndPushMessage.analyze(saveList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_YHZH,"dfzh");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(saveList).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
}