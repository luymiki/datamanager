package com.anluy.admin.web.cft;

import com.alibaba.fastjson.JSON;
import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.CftRegInfo;
import com.anluy.admin.entity.CftTrades;
import com.anluy.admin.service.AnalyzeCodeAndPushMessage;
import com.anluy.admin.service.AttachmentService;
import com.anluy.admin.web.cft.parser.CftRegParser;
import com.anluy.admin.web.cft.parser.CftTradesParser;
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
 * 功能说明：财付通流水文件操作
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/cft/trades")
@Api(value = "/api/admin/cft/trades", description = "财付通流水文件操作")
public class CftTradesParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CftTradesParserController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private FileManagerConfig fileManagerConfig;
    @Resource
    private AnalyzeCodeAndPushMessage analyzeCodeAndPushMessage;
    /**
     * 财付通流水文件保存
     *
     * @return
     */
    @ApiOperation(value = "财付通流水文件保存", response = Result.class)
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
            if(StringUtils.isBlank(attachment.getFolder())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"财付通id为空"));
            }
            String uploadDir = fileManagerConfig.getUploadDir();
            String path = uploadDir + attachment.getPath();
            CftTradesParser parser = new CftTradesParser(attachment.getId());
            List<CftTrades> regInfo = parser.parser(attachment,path);
            List<Map> saveMapList = new ArrayList<>();
            Set<String> zhSet = new HashSet<>();
            regInfo.forEach(cftTrades -> {
                cftTrades.setId(UUID.randomUUID().toString());
                cftTrades.setCreateTime(new Date());
                cftTrades.setCftId(attachment.getFolder());//借用folder这个字段来方id
                Map<String, Object> jsonMap = (Map<String, Object>)JSON.toJSON(cftTrades);
                jsonMap.forEach((k,v)->{
                    switch (k){
//                        case "create_time":{
//                            if(v!=null){
//                                jsonMap.put(k, DateFormatUtils.format((Date)v,"yyyy-MM-dd HH:mm:ss"));
//                            }
//                            break;
//                        }
                        case "tags":{
                            if(v!=null){
                                jsonMap.put(k,((String)v).split(","));
                            }
                            break;
                        }
                    }
                });
                jsonMap.put("_id",cftTrades.getId());
                saveMapList.add(jsonMap);
                if(!zhSet.contains((String)jsonMap.get("zh"))) {
                    zhSet.add((String) jsonMap.get("zh"));
                }
            });

            elasticsearchRestClient.batchSave(saveMapList,"cfttrades");
            if(!zhSet.isEmpty()){

                List<Map> zhMapList = new ArrayList<>();
                zhSet.forEach(str->{
                    Map<String,String> zhMap = new HashMap<>();
                    zhMap.put("zh",str);
                    zhMap.put("file_id",attachment.getId());
                    zhMapList.add(zhMap);
                });
                analyzeCodeAndPushMessage.analyze(zhMapList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_QQ,"zh");
                analyzeCodeAndPushMessage.analyze(zhMapList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_WEIXIN,"zh");
            }

//            analyzeCodeAndPushMessage.analyze(saveMapList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_QQ,"fsf");
//            analyzeCodeAndPushMessage.analyze(saveMapList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_WEIXIN,"fsf");
//            analyzeCodeAndPushMessage.analyze(saveMapList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_QQ,"jsf");
//            analyzeCodeAndPushMessage.analyze(saveMapList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_WEIXIN,"jsf");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(regInfo).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

}