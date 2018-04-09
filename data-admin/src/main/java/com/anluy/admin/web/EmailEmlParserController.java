package com.anluy.admin.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.Email;
import com.anluy.admin.service.AttachmentService;
import com.anluy.admin.utils.EmailEmlParser;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.web.Result;
import io.swagger.annotations.*;
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
@RequestMapping("/api/admin/email")
@Api(value = "/api/admin/email", description = "Email文件操作")
public class EmailEmlParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailEmlParserController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private FileManagerConfig fileManagerConfig;

    /**
     * 解析
     *
     * @return
     */
    @ApiOperation(value = "解析文件", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "解析文件失败")})//错误码说明
    @RequestMapping(value = "/parser", method = RequestMethod.POST)
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
            EmailEmlParser emailEml = new EmailEmlParser(uploadDir,attachment.getId());
            Email email = emailEml.parser(path);
            email.setTags(attachment.getTags());
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("解析成功").setData(email).setPath(request.getRequestURI()));
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
    @ApiOperation(value = "email文件保存", response = Result.class)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Object save(HttpServletRequest request, @RequestBody Email email) {
        try {
            if (StringUtils.isBlank(email.getSubject())) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "邮件标题为空"));
            }
            if (StringUtils.isBlank(email.getSuspName())) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001, "嫌疑人姓名为空"));
            }
            if (StringUtils.isBlank(email.getSuspId())) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001, "嫌疑人id为空"));
            }
            if (StringUtils.isBlank(email.getFileId())) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001, "邮件文件id为空"));
            }
            email.setId(UUID.randomUUID().toString());
            email.setCreateTime(new Date());
            Map<String, Object> jsonMap = (Map<String, Object>) JSON.toJSON(email);
            jsonMap.forEach((k, v) -> {
                switch (k) {
                    case "received_date":
                    case "create_time": {
                        if (v != null) {
                            jsonMap.put(k, DateFormatUtils.format((Date) v, "yyyy-MM-dd HH:mm:ss"));
                        }
                        break;
                    }
                    case "file_list": {
                        if (v != null) {
                            List<String> fileList = new ArrayList();
                            for (Object file : (List) v) {
                                fileList.add(JSON.toJSONString(file));
                            }
                            jsonMap.put(k, fileList);
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
            elasticsearchRestClient.save(jsonMap, email.getId(), "email");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(email).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
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

            elasticsearchRestClient.remove(id, "email");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("删除成功").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("删除失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
        }
    }
}