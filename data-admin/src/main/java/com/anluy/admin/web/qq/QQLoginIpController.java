package com.anluy.admin.web.qq;

import com.alibaba.fastjson.JSON;
import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.*;
import com.anluy.admin.service.AnalyzeCodeAndPushMessage;
import com.anluy.admin.service.AttachmentService;
import com.anluy.admin.utils.IPAddrUtil;
import com.anluy.admin.web.qq.parser.QQLoginIpParser;
import com.anluy.admin.web.qq.parser.QQRegParser;
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
 * 功能说明：QQ空间照片操作
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/qq/loginip")
@Api(value = "/api/admin/qq/loginip", description = "QQ登录信息操作")
public class QQLoginIpController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QQLoginIpController.class);
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
            QQLoginIpParser parser = new QQLoginIpParser(attachment.getId(),ipAddrUtil);
            QQLoginInfo loginInfo = parser.parser(attachment,path);
            loginInfo.setTags(attachment.getTags());
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("解析成功").setData(loginInfo).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
    /**
     * 保存QQ登录信息
     *
     * @return
     */
    @ApiOperation(value = "保存QQ登录信息", response = Result.class)
    @RequestMapping(value = "/save",method =RequestMethod.POST)
    public Object save(HttpServletRequest request, @RequestBody QQLoginInfo loginInfo) {
        try {
            if(StringUtils.isBlank(loginInfo.getQq())){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"qq号码为空"));
            }
            if(StringUtils.isBlank(loginInfo.getSuspName())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"嫌疑人姓名为空"));
            }
            if(StringUtils.isBlank(loginInfo.getSuspId())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"嫌疑人id为空"));
            }
            if(loginInfo.getFileId() == null ||loginInfo.getFileId().isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"文件id为空"));
            }

            loginInfo.setCreateTime(new Date());
            List<QQLoginIpInfo> ipinfoList = loginInfo.getIpinfoList();
            loginInfo.setIpinfoList(null);

            Map<String, Object> jsonMap = (Map<String, Object>)JSON.toJSON(loginInfo);
            jsonMap.forEach((k,v)->{
                switch (k){
                    case "create_time":{
                        if(v!=null){
                            jsonMap.put(k, DateFormatUtils.format((Date)v,"yyyy-MM-dd HH:mm:ss"));
                        }
                        break;
                    }
                }
            });

            //新增
            if(StringUtils.isBlank(loginInfo.getId())){
                loginInfo.setId(UUID.randomUUID().toString() );
                jsonMap.put("id",loginInfo.getId());
                jsonMap.remove("ipinfoList");
                saveIpinfoList(ipinfoList,loginInfo);
                elasticsearchRestClient.save(jsonMap,loginInfo.getId(),"qqloginip");
            }
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(jsonMap).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

    /**
     * 批量保存登录日志信息
     * @param ipinfoList
     * @param loginInfo
     */
    private void saveIpinfoList(List<QQLoginIpInfo> ipinfoList,QQLoginInfo loginInfo){
        if(ipinfoList!=null && !ipinfoList.isEmpty()){
            List<Map> subList = new ArrayList<>();
            List<String> idList = new ArrayList<>();
            for (QQLoginIpInfo ipInfo :ipinfoList                 ) {
                ipInfo.setLoginId(loginInfo.getId());
                ipInfo.setId(UUID.randomUUID().toString());
                idList.add(ipInfo.getId());
                Map<String, Object> jsonMap = (Map<String, Object>)JSON.toJSON(ipInfo);
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
                jsonMap.put("_id",ipInfo.getId());
                subList.add(jsonMap);

                if(subList.size()==10000){
                    elasticsearchRestClient.batchSave(subList,"qqloginip_list");
                    analyzeCodeAndPushMessage.analyze(subList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_IP,"ip");
                    subList.clear();
                    idList.clear();
                }
            }
            if(subList.size()>0){
                elasticsearchRestClient.batchSave(subList,"qqloginip_list");
                analyzeCodeAndPushMessage.analyze(subList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_IP,"ip");
                subList.clear();
                idList.clear();
            }
        }
    }

    /**
     * QQ登录信息删除
     *
     * @return
     */
    @ApiOperation(value = "QQ登录信息删除", response = Result.class)
    @RequestMapping(value = "/delete",method =RequestMethod.POST)
    public Object delete(HttpServletRequest request,String id,String fileId) {
        try {
            if(StringUtils.isBlank(id)){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"登录信息id为空"));
            }
            if(StringUtils.isBlank(fileId)){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"文件id为空"));
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
            String deleteDsl = "{\"query\": { \"match\": {\"login_id\": \""+id+"\"}}}";
            elasticsearchRestClient.deleteByQuery(deleteDsl,"qqloginip_list");
            elasticsearchRestClient.remove(id,"qqloginip");
            //elasticsearchRestClient.batchDelete(null,"qqloginip_list");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("删除成功").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("删除失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

}