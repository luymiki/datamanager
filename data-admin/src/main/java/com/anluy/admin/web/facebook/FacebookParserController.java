package com.anluy.admin.web.facebook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.utils.MD5;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 功能说明：facebook数据文件导入操作
 * <p>
 * Created by hc.zeng on 2021/5/28.
 */
@RestController
@RequestMapping("/api/admin/facebook")
@Api(value = "/api/admin/facebook", description = "facebook数据文件导入操作")
public class FacebookParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FacebookParserController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    /**
     * facebook数据解析保存文件
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
                fileList.addAll(FileUtils.listFiles(file, null, true));
            } else {
                fileList.add(file);
            }
            FaceBookParse parse = new FaceBookParse();
            executor = new ThreadPoolExecutor(5, 10, 60000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1024), new ThreadPoolExecutor.CallerRunsPolicy());
            System.out.println("开始时间：" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            String indexName = "facebookinfo";
            for (File f : fileList) {
                int ri = 0;
                LOGGER.info("解析文件：" + f.getAbsolutePath());
                String encoding = com.anluy.admin.utils.FileUtils.detectorCharset(f);
                LOGGER.info("文件编码：" + encoding);
                LineIterator lineIterator = new LineIterator(new BufferedReader(new InputStreamReader(FileUtils.openInputStream(f), Charsets.toCharset(encoding)), 10 * 1024 * 1024));
                List<Map> data = new ArrayList<>();
                String country = f.getName().replace(".txt","");
                while (lineIterator.hasNext()) {
                    String line = lineIterator.next();
                    ri++;
                    JSONObject o = parse.parse(country, line);
                    if (o == null || o.isEmpty()) {
                        continue;
                    }
                    data.add(o);
                    if (data.size() > BS) {
                        executor.execute(new SaveRun(data, indexName));
                        data = new ArrayList<>();
                    }
                }
                if (!data.isEmpty()) {
                    executor.execute(new SaveRun(data, indexName));
                }
                rt.put(f.getAbsolutePath(), ri);
                lineIterator.close();
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

    private class SaveRun implements Runnable {
        List<Map> data;
        String indexName;

        public SaveRun(List<Map> data, String indexName) {
            this.data = data;
            this.indexName = indexName;
        }

        @Override
        public void run() {
            try {
                elasticsearchRestClient.batchSave(data, indexName, false);
            } catch (Exception e) {
                LOGGER.error(JSONArray.toJSONString(data));
                LOGGER.error(e.toString(), e);
            }
        }
    }

    private static class FaceBookParse {
        public JSONObject parse(String country, String line) {
            JSONObject d = _parse( line);
            if (d == null || d.isEmpty()) {
                return null;
            }
            String id = MD5.encode(d.toJSONString());
            d.put("id", id);
            d.put("country", country);
            d.put("_id", id);
            d.put("create_time", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            return d;
        }
        public JSONObject _parse( String line) {
            String[] items = line.split(":");
            return foreignmigrate(items);
        }

        private JSONObject mapping(String[] items, Map<String, Integer> mapping) {
            JSONObject jsonObject = new JSONObject();
            mapping.forEach((it, inx) -> jsonObject.put(it, items[inx]));
            return jsonObject;
        }

        private JSONObject foreignmigrate( String[] items) {
            if (items.length < 10) {
                return null;
            }
            return mapping(items, foreignmigrateMap);
        }

        private static final Map<String, Integer> foreignmigrateMap = new HashMap<>();

        static {
            foreignmigrateMap.put("xh", 0);
            foreignmigrateMap.put("uid", 1);
            foreignmigrateMap.put("name", 2);
            foreignmigrateMap.put("family", 3);
            foreignmigrateMap.put("sex", 4);
            foreignmigrateMap.put("addr1", 5);
            foreignmigrateMap.put("addr2", 6);
            foreignmigrateMap.put("addr3", 7);
            foreignmigrateMap.put("profession", 8);
            foreignmigrateMap.put("describe", 8);
            foreignmigrateMap.put("reg_time", 9);
        }

    }
}