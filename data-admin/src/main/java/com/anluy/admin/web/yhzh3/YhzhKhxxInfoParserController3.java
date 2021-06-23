package com.anluy.admin.web.yhzh3;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.FileManagerConfig;
import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.TjfxYhzhJyls;
import com.anluy.admin.entity.YhzhKhxxInfo;
import com.anluy.admin.service.AnalyzeCodeAndPushMessage;
import com.anluy.admin.service.AttachmentService;
import com.anluy.admin.service.TjfxYhzhService;
import com.anluy.admin.utils.MD5;
import com.anluy.admin.web.AuthorizationController;
import com.anluy.admin.web.yhzh3.parser3.YhzhJylsInfo3;
import com.anluy.admin.web.yhzh3.parser3.YhzhJylsParser3;
import com.anluy.admin.web.yhzh3.parser3.YhzhKhxxInfo3;
import com.anluy.admin.web.yhzh3.parser3.YhzhKhxxParser3d1;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 功能说明：银行账户信息文件操作
 * <p>
 * Created by hc.zeng on 2018/6/24.
 */
@RestController("yhzhKhxxInfoParserController3")
@RequestMapping("/api/admin/yhzh3/khxx")
@Api(value = "/api/admin/yhzh3/khxx", description = "银行账户信息文件操作")
public class YhzhKhxxInfoParserController3 {
    private static final Logger LOGGER = LoggerFactory.getLogger(YhzhKhxxInfoParserController3.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private FileManagerConfig fileManagerConfig;
    @Resource
    private AnalyzeCodeAndPushMessage analyzeCodeAndPushMessage;
    @Resource
    private TjfxYhzhService tjfxYhzhService;

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
            LOGGER.info("开始时间：" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            String uploadDir = fileManagerConfig.getUploadDir();
            String path = uploadDir + attachment.getPath();
            YhzhKhxxParser3d1 parser = new YhzhKhxxParser3d1(attachment);
            YhzhJylsParser3 parserJyls = new YhzhJylsParser3(attachment);
            List<YhzhKhxxInfo3> yhzhKhxxInfoList = parser.parser(attachment.getFolder(),path,1);
            List<YhzhJylsInfo3> yhzhJylsInfoList = parserJyls.parser(attachment.getFolder(),path,2);
            List<Map> saveList = _save(attachment,yhzhKhxxInfoList,yhzhJylsInfoList,request);
            LOGGER.info("结束时间：" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功").setData(saveList).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

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
            String deleteDsl  = String.format(QTYH,map.get("ssyh"),map.get("kh"),map.get("zh"));
            elasticsearchRestClient.deleteByQuery(deleteDsl,"yhzh_jyls");
            elasticsearchRestClient.remove(id,"yhzh_khxx");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("删除成功").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("删除失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

    /**
     * 数据解析保存文件
     *
     * @return
     */
    @ApiOperation(value = "解析保存文件", response = Result.class)
    @RequestMapping(value = "/parser", method = RequestMethod.POST)
    public Object parser(HttpServletRequest request, @RequestParam String path,
                         @RequestParam(required = false) String batchSize) {
        JSONObject rt = new JSONObject();
        ThreadPoolExecutor executor = null;
        try {
            if (StringUtils.isBlank(path)) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "文件路径为空,rt=" + rt));
            }
            Integer BS = 10000;
            if (StringUtils.isNotBlank(batchSize)) {
                BS = Integer.valueOf(batchSize);
            }

            File file = new File(path);
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "文件不存在,rt=" + rt));
            }

            List<File> fileList = new ArrayList<>();
            if (file.isDirectory()) {
                fileList.addAll(FileUtils.listFiles(file, new String[]{"xls","xlsx"}, true));
            } else {
                fileList.add(file);
            }
            for (File f : fileList) {
                Attachment attachment = new Attachment();
                attachment.setSuspId(MD5.encode(f.getAbsolutePath()));
                attachment.setSuspName("IMPORT");
                attachment.setId(attachment.getSuspId());
                attachment.setPath(f.getAbsolutePath());
                attachment.setFolder("IMPORT");
                int ri = 0;
                LOGGER.info("解析文件：" + f.getAbsolutePath());
                LOGGER.info("开始时间：" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                YhzhKhxxParser3d1 parser = new YhzhKhxxParser3d1(attachment);
                YhzhJylsParser3 parserJyls = new YhzhJylsParser3(attachment);
                List<YhzhKhxxInfo3> yhzhKhxxInfoList = parser.parser(attachment.getFolder(),f.getAbsolutePath(),1);
                List<YhzhJylsInfo3> yhzhJylsInfoList = parserJyls.parser(attachment.getFolder(),f.getAbsolutePath(),2);
                _save(attachment,yhzhKhxxInfoList,yhzhJylsInfoList,request);
                rt.put(f.getAbsolutePath(), yhzhJylsInfoList.size());
                LOGGER.info("结束时间：" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功,rt=" + rt).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "rt=" + rt + "\n" + exception.getMessage()));
        } finally {
            if (executor != null) {
                executor.shutdown();
            }
        }
    }
     private  List<Map> _save(Attachment attachment,List<YhzhKhxxInfo3> yhzhKhxxInfoList, List<YhzhJylsInfo3> yhzhJylsInfoList,HttpServletRequest request) throws IOException {
         List<Map> saveList = new ArrayList<>();
         yhzhKhxxInfoList.forEach(regInfo->{
             regInfo.setId(UUID.randomUUID().toString() );
             regInfo.setCreateTime(new Date());
             regInfo.setSsyh(attachment.getFolder());
             Map<String, Object> jsonMap = (Map<String, Object>)JSON.toJSON(regInfo);
             jsonMap.remove("zhztt");
             jsonMap.put("_id",regInfo.getId());
             jsonMap.forEach((k,v)->{
                 switch (k){
                     case "tags":{
                         if(v != null){
                             jsonMap.put(k,((String)v).split(","));
                         }
                         break;
                     }
                 }
             });
             saveList.add(jsonMap);
         });
         List<Map> jylsList = new ArrayList<>();
         yhzhJylsInfoList.forEach(regInfo->{
             String md5 = MD5.encode(JSON.toJSONString(regInfo));
//                regInfo.setId(UUID.randomUUID().toString() );
             regInfo.setId(md5);//用MD5做主键，去重数据
             regInfo.setCreateTime(new Date());
             regInfo.setSsyh(attachment.getFolder());
             Map<String, Object> jsonMap = (Map<String, Object>)JSON.toJSON(regInfo);
             jsonMap.remove("jyrq");
             jsonMap.remove("jyrq2");
             jsonMap.remove("jysjStr");
             jsonMap.remove("jysjStr2");
             jsonMap.remove("jyje2");
             jsonMap.put("_id",regInfo.getId());
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
             jylsList.add(jsonMap);
         });

         elasticsearchRestClient.batchSave(saveList,"yhzh_khxx");
         if(jylsList!=null && !jylsList.isEmpty()){
             elasticsearchRestClient.batchSave(jylsList,"yhzh_jyls");
         }

         String token = request.getHeader(AuthorizationController.AUTHORIZATION);
         for (Map map :saveList ) {
             TjfxYhzhJyls tjfxCftJyls = (TjfxYhzhJyls)tjfxYhzhService.analyzeJyls(attachment.getFolder(),(String) map.get("kh"),(String) map.get("zh"),null,null,null,token);
             int ljjybs = 0;
             if(tjfxCftJyls!=null){
                 ljjybs = tjfxCftJyls.getLjjybs();
             }
             String id = (String)map.get("id");
             Map um = new HashMap();
             um.put("ljjybs",ljjybs);
             elasticsearchRestClient.update(um,id,"yhzh_khxx");
         }

         analyzeCodeAndPushMessage.analyze(saveList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_YHZH,"kh");
         analyzeCodeAndPushMessage.analyze(saveList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_YHZH,"zh");
         analyzeCodeAndPushMessage.analyze(saveList, AnalyzeCodeAndPushMessage.ANALYZE_TYPE_PHONE,"lxsj");
         return saveList;
     }
}