package com.anluy.admin.web.cft;

import com.alibaba.fastjson.JSON;
import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.*;
import com.anluy.admin.service.AnalyzeCodeAndPushMessage;
import com.anluy.admin.service.AttachmentService;
import com.anluy.admin.web.cft.parser.CftRegParser;
import com.anluy.admin.web.weixin.parser.WeiXinRegParser;
import com.anluy.commons.BaseEntity;
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
 * 功能说明：财付通文件操作
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/cft")
@Api(value = "/api/admin/cft", description = "财付通文件操作")
public class CftRegParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CftRegParserController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private FileManagerConfig fileManagerConfig;
    @Resource
    private AnalyzeCodeAndPushMessage analyzeCodeAndPushMessage;
    /**
     * 解析
     *
     * @return
     */
    @ApiOperation(value = "解析文件", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "解析文件失败")})//错误码说明
    @RequestMapping(value = "/parser", method = RequestMethod.POST)
    public @ResponseBody
    Object parser(HttpServletRequest request, @RequestBody  Attachment attachment) {
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
            CftRegParser parser = new CftRegParser(attachment.getId());
            CftRegInfo regInfo = parser.parser(path);
            regInfo.setSuspId(attachment.getSuspId());
            regInfo.setSuspName(attachment.getSuspName());
            regInfo.setTags(attachment.getTags());
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("解析成功").setData(regInfo).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    /**
     * 财付通文件保存
     *
     * @return
     */
    @ApiOperation(value = "财付通文件保存", response = Result.class)
    @RequestMapping(value = "/save",method =RequestMethod.POST)
    public Object save(HttpServletRequest request, @RequestBody CftRegInfo regInfo) {
        try {
            if(StringUtils.isBlank(regInfo.getZh())){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"账号为空"));
            }
            if(StringUtils.isBlank(regInfo.getSuspName())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"可疑人姓名为空"));
            }
            if(StringUtils.isBlank(regInfo.getSuspId())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"可疑人id为空"));
            }
            if(StringUtils.isBlank(regInfo.getFileId())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"文件id为空"));
            }
            regInfo.setId(UUID.randomUUID().toString() );
            regInfo.setCreateTime(new Date());

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
            elasticsearchRestClient.save(jsonMap,regInfo.getId(),"cftreginfo");
            List<Map> rlist = new ArrayList<>();
            rlist.add(jsonMap);
            analyzeCodeAndPushMessage.analyze(rlist, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_QQ,"zh");
            analyzeCodeAndPushMessage.analyze(rlist, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_WEIXIN,"zh");
            analyzeCodeAndPushMessage.analyze(rlist, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_PHONE,"dh");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(regInfo).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

    /**
     * 财付通文件删除
     *
     * @return
     */
    @ApiOperation(value = "财付通文件删除", response = Result.class)
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

            String deleteDsl = "{\"query\": { \"match\": {\"cft_id\": \""+id+"\"}}}";
            elasticsearchRestClient.deleteByQuery(deleteDsl,"cfttrades");
            elasticsearchRestClient.remove(id,"cftreginfo");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("删除成功").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("删除失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
}