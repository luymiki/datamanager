package com.anluy.admin.web.weixin;

import com.alibaba.fastjson.JSON;
import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.*;
import com.anluy.admin.service.AnalyzeCodeAndPushMessage;
import com.anluy.admin.service.AttachmentService;
import com.anluy.admin.utils.IPAddrUtil;
import com.anluy.admin.web.qq.parser.QQRegParser;
import com.anluy.admin.web.weixin.parser.WeiXinRegParser;
import com.anluy.admin.web.weixin.parser.WeiXinRegParser2;
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
 * 功能说明：微信文件操作
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/weixin")
@Api(value = "/api/admin/weixin", description = "微信文件操作")
public class WeiXinRegParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeiXinRegParserController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private FileManagerConfig fileManagerConfig;
    @Resource
    private AnalyzeCodeAndPushMessage analyzeCodeAndPushMessage;

    @Resource
    private IPAddrUtil ipAddrUtil;
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
            WeiXinRegParser parser = new WeiXinRegParser(attachment.getId(),ipAddrUtil);
            WxregInfo regInfo = parser.parser(attachment,path);
            if(regInfo ==null){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"没有注册信息文件"));
            }
            regInfo.setTags(attachment.getTags());
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("解析成功").setData(regInfo).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
    /**
     * 解析
     *
     * @return
     */
    @ApiOperation(value = "解析文件", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "解析文件失败")})//错误码说明
    @RequestMapping(value = "/parser2", method = RequestMethod.POST)
    public @ResponseBody
    Object parser2(HttpServletRequest request, @RequestBody  List<Attachment> attachments) {
        try {
            if (attachments == null || attachments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001, "文件为空"));
            }
            Attachment regAttachment = null;
            //找注册信息文件
            for (Attachment attachment : attachments) {
                if(attachment.getName().indexOf("RegInfo")>=0){
                    regAttachment = attachment;
                }
            }
            if (regAttachment == null) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001, "文件为空"));
            }
            if(StringUtils.isBlank(regAttachment.getId())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"文件id为空"));
            }
            if(StringUtils.isBlank(regAttachment.getPath())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"文件路径为空"));
            }
            if(StringUtils.isBlank(regAttachment.getSuffix())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"文件类型为空"));
            }
            WeiXinRegParser2 parser = new WeiXinRegParser2(ipAddrUtil,fileManagerConfig);
            WxregInfo regInfo = parser.parser(attachments);
            if(regInfo ==null){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"没有注册信息文件"));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("解析成功").setData(regInfo).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    /**
     * email文件保存
     *
     * @return
     */
    @ApiOperation(value = "注册文件保存", response = Result.class)
    @RequestMapping(value = "/save",method =RequestMethod.POST)
    public Object save(HttpServletRequest request, @RequestBody WxregInfo regInfo) {
        try {
//            if(StringUtils.isBlank(regInfo.getQq())){
//                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"qq号码为空"));
//            }
            if(StringUtils.isBlank(regInfo.getSuspName())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"嫌疑人姓名为空"));
            }
            if(StringUtils.isBlank(regInfo.getSuspId())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"嫌疑人id为空"));
            }
            if(StringUtils.isBlank(regInfo.getFileId())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"文件id为空"));
            }
            regInfo.setId(UUID.randomUUID().toString() );
            regInfo.setCreateTime(new Date());

            List<Wxlxr> wxlxrList = regInfo.getWxlxrList();
            List<Wxqun> wxqunList = regInfo.getWxqunList();
            List<Wxloginip> wxloginipList = regInfo.getWxloginipList();
            regInfo.setWxlxrList(null);
            regInfo.setWxqunList(null);
            regInfo.setWxloginipList(null);

            Set<String> ipSet = new HashSet<>();
            if(wxloginipList!=null && !wxloginipList.isEmpty()){
                wxloginipList.forEach(wxloginip -> {
                    if(!ipSet.contains(wxloginip.getIp())){
                        ipSet.add(wxloginip.getIp());
                    }
                });
            }

            Map<String, Object> jsonMap = (Map<String, Object>)JSON.toJSON(regInfo);
            jsonMap.remove("wxloginipList");
            jsonMap.remove("wxqunList");
            jsonMap.remove("wxlxrList");
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
            jsonMap.put("ip_list",ipSet);

            if(wxlxrList!=null && !wxlxrList.isEmpty()){
                List<Map> wxlxrMapList = new ArrayList<>();
                wxlxrList.forEach(w->{
                    w.setInfoId(regInfo.getId());
                    Map<String, Object> m = json(w);
                    wxlxrMapList.add(m);
                });
                elasticsearchRestClient.batchSave(wxlxrMapList,"wxlxr");
                analyzeCodeAndPushMessage.analyze(wxlxrMapList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_WEIXIN,"zh");
                analyzeCodeAndPushMessage.analyze(wxlxrMapList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_QQ,"qq");
            }
            if(wxqunList!=null && !wxqunList.isEmpty()){
                List<Map> wxqunMapList = new ArrayList<>();
                wxqunList.forEach(w->{
                    w.setInfoId(regInfo.getId());
                    Map<String, Object> m = json(w);
                    wxqunMapList.add(m);
                });
                elasticsearchRestClient.batchSave(wxqunMapList,"wxqun");
//                analyzeCodeAndPushMessage.analyze(wxqunMapList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_WEIXIN,"zh");
            }
            if(wxloginipList!=null && !wxloginipList.isEmpty()){
                List<Map> wxloginipMapList = new ArrayList<>();
                wxloginipList.forEach(w->{
                    w.setInfoId(regInfo.getId());
                    Map<String, Object> m = json(w);
                    wxloginipMapList.add(m);
                });
                elasticsearchRestClient.batchSave(wxloginipMapList,"wxloginip");
                analyzeCodeAndPushMessage.analyze(wxloginipMapList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_IP,"ip");
            }

            elasticsearchRestClient.save(jsonMap,regInfo.getId(),"wxreginfo");
            analyzeCodeAndPushMessage.analyze(jsonMap, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_WEIXIN,"weixin");
            analyzeCodeAndPushMessage.analyze(jsonMap, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_QQ,"qq");
            analyzeCodeAndPushMessage.analyze(jsonMap, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_EMAIL,"email");
            analyzeCodeAndPushMessage.analyze(jsonMap, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_PHONE,"dh");

            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(regInfo).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

    private Map<String, Object> json(BaseEntity w){
        w.setId(UUID.randomUUID().toString() );
        Map<String, Object> m = (Map<String, Object>)JSON.toJSON(w);
        m.put("_id",w.getId());
        return m;
    }

    /**
     * email文件删除
     *
     * @return
     */
    @ApiOperation(value = "注册文件删除", response = Result.class)
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
            String deleteDsl = "{\"query\": { \"match\": {\"info_id\": \""+id+"\"}}}";
            elasticsearchRestClient.deleteByQuery(deleteDsl,"wxlxr");
            elasticsearchRestClient.deleteByQuery(deleteDsl,"wxqun");
            elasticsearchRestClient.deleteByQuery(deleteDsl,"wxloginip");
            elasticsearchRestClient.remove(id,"wxreginfo");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("删除成功").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("删除失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
}