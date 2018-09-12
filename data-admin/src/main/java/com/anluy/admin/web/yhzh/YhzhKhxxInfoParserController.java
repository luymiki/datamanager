package com.anluy.admin.web.yhzh;

import com.alibaba.fastjson.JSON;
import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.YhzhKhxxInfo;
import com.anluy.admin.entity.ZfbJyjlInfo;
import com.anluy.admin.service.AnalyzeCodeAndPushMessage;
import com.anluy.admin.service.AttachmentService;
import com.anluy.admin.web.yhzh.parser.YhzhKhxxParser;
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
import java.io.File;
import java.util.*;

/**
 * 功能说明：银行账户信息文件操作
 * <p>
 * Created by hc.zeng on 2018/6/24.
 */
@RestController
@RequestMapping("/api/admin/yhzh/khxx")
@Api(value = "/api/admin/yhzh/khxx", description = "银行账户信息文件操作")
public class YhzhKhxxInfoParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(YhzhKhxxInfoParserController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private FileManagerConfig fileManagerConfig;
    @Resource
    private AnalyzeCodeAndPushMessage analyzeCodeAndPushMessage;

    /**
     * 银行账户交易记录文件保存
     *
     * @return
     */
    @ApiOperation(value = "银行账户交易记录文件保存", response = Result.class)
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
            YhzhKhxxParser parser = new YhzhKhxxParser(attachment);
            List<YhzhKhxxInfo> zfbJyjlInfoList = parser.parser(attachment.getFolder(),path);
            List<Map> saveList = new ArrayList<>();
            zfbJyjlInfoList.forEach(regInfo->{
                regInfo.setId(UUID.randomUUID().toString() );
                regInfo.setCreateTime(new Date());
                regInfo.setSsyh(attachment.getFolder());
                Map<String, Object> jsonMap = (Map<String, Object>)JSON.toJSON(regInfo);
                jsonMap.put("_id",regInfo.getId());
                jsonMap.forEach((k,v)->{
                    switch (k){
                        case "create_time":{
                            if(v!=null){
                                jsonMap.put(k, DateFormatUtils.format((Date)v,"yyyy-MM-dd HH:mm:ss"));
                            }
                            break;
                        }
                        case "xhrq":
                        case "khrq":{
                            if(v!=null){
                                jsonMap.put(k, DateFormatUtils.format((Date)v,"yyyy-MM-dd"));
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

            elasticsearchRestClient.batchSave(saveList,"yhzh_khxx");
            analyzeCodeAndPushMessage.analyze(saveList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_YHZH,"kh");
            analyzeCodeAndPushMessage.analyze(saveList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_YHZH,"zh");
            analyzeCodeAndPushMessage.analyze(saveList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_PHONE,"lxsj");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(saveList).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

    private static final  String NYYH = "{\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"should\":[{\"term\":{\"ssyh\":\"农业银行\"}}]}},{\"bool\":{\"should\":[{\"term\":{\"kh\":\"%s\"}}]}}]}}]}}}";
    private static final  String QTYH =  "{\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"should\":[{\"term\":{\"ssyh\":\"%s\"}}]}},{\"bool\":{\"should\":[{\"term\":{\"kh\":\"%s\"}}]}},{\"bool\":{\"should\":[{\"term\":{\"zh\":\"%s\"}}]}}]}}]}}}";
    /**
     * 银行账户文件删除
     *
     * @return
     */
    @ApiOperation(value = "银行账户文件删除", response = Result.class)
    @RequestMapping(value = "/delete",method =RequestMethod.POST)
    public Object delete(HttpServletRequest request,String id,String fileId) {
        try {
            if(StringUtils.isBlank(fileId)){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"文件id为空"));
            }
            if(StringUtils.isBlank(id)){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"银行账户id为空"));
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
            Map map = elasticsearchRestClient.get("yhzh_khxx",id,null,null);
            String deleteDsl = null;
            if("农业银行".equals(map.get("ssyh"))){
                deleteDsl = String.format(NYYH,map.get("kh"));
            }else {
                deleteDsl = String.format(QTYH,map.get("ssyh"),map.get("kh"),map.get("zh"));
            }
            elasticsearchRestClient.deleteByQuery(deleteDsl,"yhzh_jyls");
            elasticsearchRestClient.remove(id,"yhzh_khxx");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("删除成功").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("删除失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
}