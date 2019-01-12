package com.anluy.admin.web.email;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.Email;
import com.anluy.admin.entity.EmailReg;
import com.anluy.admin.service.AnalyzeCodeAndPushMessage;
import com.anluy.admin.service.AttachmentService;
import com.anluy.admin.utils.IPAddrUtil;
import com.anluy.admin.web.email.parser.EmailEmlParser;
import com.anluy.admin.web.email.parser.EmailRegParser;
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
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/email_reg")
@Api(value = "/api/admin/email_reg", description = "Email注册信息")
public class EmailRegParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailRegParserController.class);
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
    @ApiOperation(value = "email文件保存", response = Result.class)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public @ResponseBody Object parser(HttpServletRequest request, @RequestBody  Attachment attachment) {
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
            EmailRegParser emailEml = new EmailRegParser(attachment.getId(),ipAddrUtil);
            List<EmailReg> emailRegList = emailEml.parser(path);
            List<Map> ipsaveList = new ArrayList<>();
            List<Map> regsaveList = new ArrayList<>();
            if(!emailRegList.isEmpty()){
                emailRegList.forEach(emailReg -> {
                    emailReg.setId(UUID.randomUUID().toString());
                    emailReg.setCreateTime(new Date());
                    emailReg.setSuspName(attachment.getSuspName());
                    emailReg.setSuspId(attachment.getSuspId());
                    emailReg.setTags(attachment.getTags());
                    Map<String, Object> jsonMap = (Map<String, Object>) JSON.toJSON(emailReg);
                    regsaveList.add(jsonMap);
                    jsonMap.put("_id",jsonMap.get("id"));
                    jsonMap.put("txt_data",emailReg.getTxtData().toString());
                    jsonMap.forEach((k, v) -> {
                        switch (k) {
//                            case "create_time": {
//                                if (v != null) {
//                                    jsonMap.put(k, DateFormatUtils.format((Date) v, "yyyy-MM-dd HH:mm:ss"));
//                                }
//                                break;
//                            }
                            case "txt_data": {
                                if (v != null) {
                                    jsonMap.put(k, v.toString());
                                }
                                break;
                            }
                            case "tags": {
                                if (v != null) {
                                    jsonMap.put(k, ((String) v).split(","));
                                }
                                break;
                            }
                        }
                    });
                    JSONArray iplist = (JSONArray) jsonMap.remove("iplist");
                    if(!iplist.isEmpty()){
                        iplist.forEach(ip->{
                            JSONObject jo = (JSONObject) ip;
                            jo.put("tags",attachment.getTags());
                            jo.put("create_time",new Date());
                            jo.put("reg_id",emailReg.getId());
                            jo.put("susp_name",attachment.getSuspName());
                            jo.put("susp_id",attachment.getSuspId());
                            jo.put("file_id",attachment.getId());
                            jo.put("id",UUID.randomUUID().toString());
                            jo.put("_id",jo.get("id"));
                            ipsaveList.add(jo);
                            jo.forEach((k, v) -> {
                                switch (k) {
//                                    case "time":
                                    case "create_time": {
                                        if (v != null) {
                                            jo.put(k, DateFormatUtils.format((Date) v, "yyyy-MM-dd HH:mm:ss"));
                                        }
                                        break;
                                    }
                                    case "tags": {
                                        if (v != null) {
                                            jo.put(k, ((String) v).split(","));
                                        }
                                        break;
                                    }
                                }
                            });
                        });
                    }
                });

                elasticsearchRestClient.batchSave(ipsaveList, "email_ip");
                elasticsearchRestClient.batchSave(regsaveList, "email_reg");

                analyzeCodeAndPushMessage.analyze(regsaveList,AnalyzeCodeAndPushMessage.ANALYZE_TYPE_EMAIL,"email");
                return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("解析成功").setPath(request.getRequestURI()));
            }else {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(404,"没有解析到数据").setPath(request.getRequestURI()));
            }
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    /**
     * email文件删除
     *
     * @return
     */
    @ApiOperation(value = "email文件删除", response = Result.class)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Object delete(HttpServletRequest request, String id, String fileId) {
        try {
            if (StringUtils.isBlank(fileId)) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "邮件文件id为空"));
            }
            if (StringUtils.isBlank(id)) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "邮件id为空"));
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

            elasticsearchRestClient.remove(id, "email_reg");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("删除成功").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("删除失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
        }
    }
}