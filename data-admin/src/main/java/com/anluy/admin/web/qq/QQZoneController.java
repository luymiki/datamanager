package com.anluy.admin.web.qq;

import com.alibaba.fastjson.JSON;
import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.QQRegInfo;
import com.anluy.admin.entity.QQZone;
import com.anluy.admin.service.AttachmentService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 功能说明：QQ空间照片操作
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/qq/qzone")
@Api(value = "/api/admin/qq/qzone", description = "QQ空间照片操作")
public class QQZoneController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QQZoneController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private FileManagerConfig fileManagerConfig;

    /**
     * 保存空间信息
     *
     * @return
     */
    @ApiOperation(value = "保存空间信息", response = Result.class)
    @RequestMapping(value = "/save",method =RequestMethod.POST)
    public Object save(HttpServletRequest request, @RequestBody QQZone qqZone) {
        try {
            if(StringUtils.isBlank(qqZone.getQq())){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"qq号码为空"));
            }
            if(StringUtils.isBlank(qqZone.getSuspName())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"嫌疑人姓名为空"));
            }
            if(StringUtils.isBlank(qqZone.getSuspId())){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"嫌疑人id为空"));
            }
            if(qqZone.getFileId() == null ||qqZone.getFileId().isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Result.error(1001,"文件id为空"));
            }

            qqZone.setCreateTime(new Date());
            Map<String, Object> jsonMap = (Map<String, Object>)JSON.toJSON(qqZone);
            jsonMap.forEach((k,v)->{
                switch (k){
//                    case "create_time":{
//                        if(v!=null){
//                            jsonMap.put(k, DateFormatUtils.format((Date)v,"yyyy-MM-dd HH:mm:ss"));
//                        }
//                        break;
//                    }
                    case "tags":{
                        if(v!=null){
                            jsonMap.put(k,((String)v).split(","));
                        }
                        break;
                    }
                }
            });

            //新增
            if(StringUtils.isBlank(qqZone.getId())){
                qqZone.setId(UUID.randomUUID().toString() );
                jsonMap.put("id",qqZone.getId());
                elasticsearchRestClient.save(jsonMap,qqZone.getId(),"qqzone");
            }else{
                //修改
                elasticsearchRestClient.update(jsonMap,qqZone.getId(),"qqzone");
            }
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(jsonMap).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
    /**
     * 空间信息删除
     *
     * @return
     */
    @ApiOperation(value = "空间信息删除", response = Result.class)
    @RequestMapping(value = "/delete",method =RequestMethod.POST)
    public Object delete(HttpServletRequest request,String id) {
        try {
            if(StringUtils.isBlank(id)){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"注册id为空"));
            }
            Map map = elasticsearchRestClient.get("qqzone",id,null,null);
            List<String> list = (List) map.get("file_id");
            if(list!=null){
                for (String fileId:list                     ) {
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
                }
            }

            elasticsearchRestClient.remove(id,"qqzone");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("删除成功").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("删除失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

    /**
     * 空间照片信息删除
     *
     * @return
     */
    @ApiOperation(value = "空间照片信息删除", response = Result.class)
    @RequestMapping(value = "/delpic",method =RequestMethod.POST)
    public Object delpic(HttpServletRequest request,String id,String fileId) {
        try {
            if(StringUtils.isBlank(fileId)){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"空间信息id为空"));
            }
            if(StringUtils.isBlank(id)){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"空间照片信息id为空"));
            }

            Map map = elasticsearchRestClient.get("qqzone",id,null,null);
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
            List list = (List) map.get("file_id");
            if(list!=null){
                list.remove(fileId);
                map.put("pic",list.size());
            }
            elasticsearchRestClient.update(map,id,"qqzone");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("删除成功").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("删除失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
}