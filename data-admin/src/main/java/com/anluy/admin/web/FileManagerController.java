package com.anluy.admin.web;

import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.Email;
import com.anluy.admin.service.AttachmentService;
import com.anluy.admin.utils.EmailEmlParser;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 功能说明：文件管理类
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@Controller
@RequestMapping("/api/admin/file")
@Api(value = "/api/admin/file", description = "文件管理类")
public class FileManagerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileManagerController.class);
    private static final String PATTERN_VIDEO = ".*\\.avi$|.*\\.rmvb$|.*\\.rm$|.*\\.asf$|.*\\.divx$|.*\\.mpg$|.*\\.mpeg$|.*\\.mpe$|.*\\.wmv$|.*\\.mp4$|.*\\.mkv$|.*\\.vob$";
    private static final String PATTERN_PIC = ".*\\.bmp$|.*\\.jpg$|.*\\.jpeg$|.*\\.png$|.*\\.gif$";
    @Resource
    private FileManagerConfig fileManagerConfig;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    /**
     * 上传文件
     *
     * @return
     */
    @ApiOperation(value = "上传文件", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "上传文件失败")})//错误码说明
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody Object upload(HttpServletRequest request, @RequestParam(required = false) MultipartFile file, String name, Long size, String folder, String tags,String suspName,String suspId) {
        try {
            if(file == null  ){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"上传的文件为空"));
            }
            if(StringUtils.isBlank(folder)){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"文件夹为空"));
            }
            if(StringUtils.isBlank(tags)){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"标签为空"));
            }
            if(StringUtils.isBlank(suspName)){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"嫌疑人姓名为空"));
            }
            if(StringUtils.isBlank(suspId)){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"嫌疑人id为空"));
            }
            String uploadDir = fileManagerConfig.getUploadDir();
            String path = "/"+suspId+"/" + UUID.randomUUID().toString() +"-"+ file.getOriginalFilename();
            File f = new File(uploadDir + path);
            if(!f.exists()){
                if(!f.getParentFile().exists()){
                    f.getParentFile().mkdirs();
                }
            }

            file.transferTo(f);
            Map<String, Object> params = new HashMap<>();
            Attachment attachment = new Attachment();
            attachment.setName(name);
            attachment.setFolder(folder);
            attachment.setTags(tags);
            attachment.setPath(path);
            attachment.setSize(size);
            attachment.setCreateTime(new Date());
            attachment.setSuspName(suspName);
            attachment.setSuspId(suspId);
//            params.put("id", UUID.randomUUID().toString());
//            params.put("name", name);
//            params.put("size", String.valueOf(size));
//            params.put("folder", folder);
//            params.put("tags", tags.split(","));
//            params.put("path", path);
//            params.put("create_time", DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
            String regName = name.toLowerCase().trim();
            if(Pattern.matches(PATTERN_VIDEO, regName)){
                //params.put("type", "视频");
                attachment.setType("视频");
            }else if(Pattern.matches(PATTERN_PIC, regName)){
                //params.put("type", "图片");
                attachment.setType("图片");
            }else if(regName.endsWith(".sql")){
                //params.put("type", "SQL文件");
                attachment.setType("SQL文件");
            }else{
                //params.put("type", "文件");
                attachment.setType("文件");
            }
            String[] ns = name.split("\\.");
            String suffix = ns[ns.length-1];
            params.put("suffix", suffix);
            attachment.setSuffix(suffix);
            //elasticsearchRestClient.save(params,(String) params.get("id"),"attachment");
            attachmentService.save(attachment);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(attachment).setPath(request.getRequestURI()));
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
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"文件id为空"));
            }
            if(StringUtils.isBlank(attachment.getSuffix())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"文件类型为空"));
            }
            String uploadDir = fileManagerConfig.getUploadDir();
            String path = uploadDir + attachment.getPath();

            //读取文件
            switch (attachment.getSuffix().toLowerCase()){
                case "eml":{//邮件
                    EmailEmlParser emailEml = new EmailEmlParser(uploadDir,attachment.getId());
                    Email email = emailEml.parser(path);
                    return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("解析成功").setData(email).setPath(request.getRequestURI()));
                }
                default:{
                    break;
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("解析成功").setData(attachment).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    /**
     * 根据文件id查询文件
     *
     * @return
     */
    @ApiOperation(value = "根据文件id查询文件", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "查询文件失败")})//错误码说明
    @RequestMapping(value = "/get", method = {RequestMethod.GET,RequestMethod.POST})
    public @ResponseBody Object get(HttpServletRequest request, String id) {
        try {

            if(StringUtils.isBlank(id)){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"文件id为空"));
            }
            Attachment attachment = attachmentService.get(id);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("查询成功").setData(attachment).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
    /**
     * 根据文件id删除文件
     *
     * @return
     */
    @ApiOperation(value = "根据文件id删除文件", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "删除文件失败")})//错误码说明
    @RequestMapping(value = "/delete", method = {RequestMethod.GET,RequestMethod.POST})
    public @ResponseBody Object delete(HttpServletRequest request, String id) {
        try {

            if(StringUtils.isBlank(id)){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"文件id为空"));
            }
            Attachment attachment = attachmentService.get(id);
            if(attachment!=null){
                String uploadDir = fileManagerConfig.getUploadDir();
                String path = uploadDir +"/"+attachment.getPath();
                File f = new File(path);
                if(f.isFile()){
                    f.delete();
                }
            }
            attachmentService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("删除成功").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
}