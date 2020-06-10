package com.anluy.admin.web.none1;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.utils.CSVReader;
import com.anluy.admin.utils.MD5;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 功能说明：未知类型1数据文件导入操作
 * <p>
 * Created by hc.zeng on 2019/11/30.
 */
@RestController
@RequestMapping("/api/admin/none1")
@Api(value = "/api/admin/none1", description = "未知类型1数据文件导入操作")
public class None1ParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(None1ParserController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    /**
     * 亚太电信数据解析保存文件
     *
     * @return
     */
    @ApiOperation(value = "解析未知类型1数据文件", response = Result.class)
    @RequestMapping(value = "/parser", method = RequestMethod.POST)
    public Object parser(HttpServletRequest request,
                         @RequestParam @ApiParam(value = "文件路径") String path,
                         @RequestParam @ApiParam(defaultValue = "membr,membrhist,pts_order_user", value = "数据文件分类membr,membrhist,pts_order_user") String type,
                         @RequestParam(required = false) @ApiParam(defaultValue = "UTF-8", value = "文件编码格式") String encoding,
                         @RequestParam(required = false) @ApiParam(defaultValue = "none1_membr,none1_membrhist,none1_pts_order_user", value = "表名，none1_membr,none1_membrhist,none1_pts_order_user选其中一个，和type的选择对应；") String indexName,
                         @RequestParam(required = false) @ApiParam(defaultValue = "10000", value = "单次批量入库记录条数") String batchSize) {
        JSONObject rt = new JSONObject();
        ThreadPoolExecutor executor = null;
        try {
            if (StringUtils.isBlank(path)) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "文件路径为空,rt=" + rt));
            }
            if (StringUtils.isBlank(type)) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "文件类型为空,rt=" + rt));
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
            None1Parse parse = None1ParseFctory.getParse(type);
            executor = new ThreadPoolExecutor(5, 10, 60000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1024), new ThreadPoolExecutor.CallerRunsPolicy());
            System.out.println("开始时间：" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

            for (File f : fileList) {
                if (!f.getName().endsWith(".sql")) {
                    continue;
                }
                int ri = 0;
                if (StringUtils.isBlank(indexName)) {
                    indexName = parse.getIndex(f.getName());
                }
                if (StringUtils.isBlank(encoding)) {
                    encoding = com.anluy.admin.utils.FileUtils.detectorCharset(f);
                }
                if (StringUtils.isBlank(indexName)) {
                    continue;
                }
                LOGGER.info("解析文件：" + f.getAbsolutePath());
                LOGGER.info("文件编码：" + encoding);
                LineIterator lineIterator = new LineIterator(new BufferedReader(new InputStreamReader(FileUtils.openInputStream(f), Charsets.toCharset(encoding)), 10 * 1024 * 1024));
                List<Map> data = new ArrayList<>();
                String ll = "";
                while (lineIterator.hasNext()) {
                    String line = lineIterator.next();
                    if (StringUtils.isBlank(line)) {
                        continue;
                    }
                    line = line.trim();
                    if (!line.endsWith(";")) {
                        ll += line;
                        continue;
                    }
                    line = ll + line;
                    ll = "";
                    ri++;
                    JSONObject o = parse.parse(type, line, f.getName(), elasticsearchRestClient);
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

    private interface None1Parse {
        JSONObject _parse(String type, String line, String fileName);

        String getIndex(String fileName);

        Map<String, Integer> _mapping();

        default JSONObject mapping(List<String> items) {
            if (items == null) {
                return null;
            }
            JSONObject jsonObject = new JSONObject();
            _mapping().forEach((it, inx) -> {
                if (inx < items.size()) {
                    jsonObject.put(it, quotaItem(items.get(inx)));
                }
            });
            return jsonObject;
        }

        default JSONObject parse(String type, String line, String fileName, ElasticsearchRestClient elasticsearchRestClient) {
            JSONObject d = _parse(type, line, fileName);
            if (d == null || d.isEmpty()) {
                return null;
            }
            String id = MD5.encode(d.toJSONString());
            d.put("create_time", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            d.put("id", id);
            d.put("_id", id);

            return d;
        }
    }

    private static class None1ParseFctory {
        public static None1Parse getParse(String type) {
            switch (type) {
                case "membr": {
                    return new MembrParse();
                }
                case "membrhist": {
                    return new MembrhistParse();
                }
                case "pts_order_user": {
                    return new PtsOrderUserParse();
                }
            }
            return null;
        }
    }

    private static class MembrParse implements None1Parse {

        @Override
        public JSONObject _parse(String type, String line, String fileName) {
            List<String> items = parserLine(line);
            return membr(items);
        }

        protected JSONObject membr(List<String> items) {
            return mapping(items);
        }

        @Override
        public Map<String, Integer> _mapping() {
            return MAPPING;
        }

        private static final Map<String, Integer> MAPPING = new HashMap<>();

        static {
            MAPPING.put("oid", 0);
            MAPPING.put("created", 1);
            MAPPING.put("modified", 2);
            MAPPING.put("membrskypeid", 3);
            MAPPING.put("membrname", 4);
            MAPPING.put("membrgender", 5);
            MAPPING.put("membraddress", 6);
            MAPPING.put("membrzip", 7);
            MAPPING.put("membrbirthday", 8);
            MAPPING.put("membremail", 9);
            MAPPING.put("membrphone", 10);
            MAPPING.put("membrtaiwanid", 11);
            MAPPING.put("membrprofile", 12);
            MAPPING.put("membrverify", 13);
            MAPPING.put("membrverifydoubted", 14);
            MAPPING.put("membrcellphone", 15);
            MAPPING.put("membracceptskypenews", 16);
            MAPPING.put("membrencryptedpassword", 17);
            MAPPING.put("memverifydate", 18);
            MAPPING.put("memverifyendate", 19);
            MAPPING.put("memverfitytype", 20);
            MAPPING.put("memverifyrole", 21);
            MAPPING.put("memlastverifiedrole", 22);
            MAPPING.put("scv_date", 23);
            MAPPING.put("scv_addr", 24);
        }

        @Override
        public String getIndex(String fileName) {
            return "none1_membr";
        }

    }

    private static class MembrhistParse implements None1Parse {

        @Override
        public JSONObject _parse(String type, String line, String fileName) {
            List<String> items = parserLine(line);
            return membrhist(items);
        }

        protected JSONObject membrhist(List<String> items) {
            return mapping(items);
        }

        @Override
        public Map<String, Integer> _mapping() {
            return MAPPING;
        }

        private static final Map<String, Integer> MAPPING = new HashMap<>();

        static {
            MAPPING.put("oid", 0);
            MAPPING.put("created", 1);
            MAPPING.put("modified", 2);
            MAPPING.put("membrhistskypeid", 3);
            MAPPING.put("membrhistname", 4);
            MAPPING.put("membrhistaddress", 5);
            MAPPING.put("membrhistemail", 6);
            MAPPING.put("membrhistcellphone", 7);
            MAPPING.put("membrhisttaiwanid", 8);
            MAPPING.put("membrhistverifycode", 9);
            MAPPING.put("membrhistverifystatus", 10);
            MAPPING.put("membrhistverifyway", 11);
            MAPPING.put("membrhistacceptskypenews", 12);
            MAPPING.put("membrhistcreateduserid", 13);
            MAPPING.put("membrhistip", 14);
            MAPPING.put("is_sa", 15);
        }

        @Override
        public String getIndex(String fileName) {
            return "none1_membrhist";
        }

    }

    private static class PtsOrderUserParse implements None1Parse {
        @Override
        public JSONObject _parse(String type, String line, String fileName) {
            List<String> items = parserLine(line);
            return ptsOrderUser(items);
        }

        protected JSONObject ptsOrderUser(List<String> items) {

            return mapping(items);
        }

        @Override
        public Map<String, Integer> _mapping() {
            return MAPPING;
        }

        private static final Map<String, Integer> MAPPING = new HashMap<>();

        static {
            MAPPING.put("membraddress1", 0);
            MAPPING.put("membraddress2", 1);
            MAPPING.put("membrbirthday", 2);
            MAPPING.put("membrcompany", 3);
            MAPPING.put("membrhisttaiwanid", 4);
            MAPPING.put("membrname", 5);
            MAPPING.put("membremail", 6);
            MAPPING.put("membrgender", 7);
            MAPPING.put("membrcellphone", 8);
            MAPPING.put("membrskypeid", 9);
            MAPPING.put("membrskypename", 10);
            MAPPING.put("membrphone", 11);
            MAPPING.put("orderid", 14);
            MAPPING.put("recipientsphone", 15);
            MAPPING.put("recipientsname", 16);
        }

        @Override
        public String getIndex(String fileName) {
            return "none1_pts_order_user";
        }
    }

    private static String quotaItem(String item) {
        return item.trim();
    }

    public static List<String> parserLine(String str) {
        try {
            str = str.replace(" ", "").replace("\n", "");
            String[] strs = str.split("VALUES");
            String ss = strs[1];
            String fstr = ss.substring(1, ss.length()).toLowerCase();
            int idx = -1;
            while ((idx = fstr.indexOf("to_date(")) != -1) {
                String d = fstr.substring(idx, fstr.indexOf(")",idx) + 1);
                String d2 = d.replace("to_date(", "").replace("'", "").replace(")", "");
                String[] dstr = d2.split(",");
                String dateStr = dstr[0];
                fstr = fstr.replace(d, dateStr);
            }
            if (fstr.endsWith(");")) {
                fstr = fstr.substring(0, fstr.length() - 2);
            }
            fstr=  fstr.replace("\\","");
            CSVReader reader = new CSVReader(null, ',', '\'');

            List<String> list = reader.parseLine(fstr);
            return list;
        } catch (Exception e1) {
            LOGGER.error("解析失败=>>" + str);
        }

        return null;
    }

    public static void main(String[] args) {
        String str = "INSERTINTO\"MEMBR\"(\"ID\",\"CREATED\",\"MODIFIED\",\"MEMBRSKYPEID\",\"MEMBRNAME\",\"MEMBRGENDER\",\"MEMBRADDRESS\",\"MEMBRZIP\",\"MEMBRBIRTHDAY\",\"MEMBREMAIL\",\"MEMBRPHONE\",\"MEMBRTAIWANID\",\"MEMBRPROFILE\",\"MEMBRVERIFY\",\"MEMBRVERIFYDOUBTED\",\"MEMBRCELLPHONE\",\"MEMBRACCEPTSKYPENEWS\",\"MEMBRENCRYPTEDPASSWORD\",\"MEMVERIFYDATE\",\"MEMVERIFYENDATE\",\"MEMVERFITYTYPE\",\"MEMVERIFYROLE\",\"MEMLASTVERIFIEDROLE\",\"SCV_DATE\",\"SCV_ADDR\")VALUES('4068',TO_DATE('20050813172406','YYYYMMDDHH24MISS'),TO_DATE('20050813181314','YYYYMMDDHH24MISS'),'chang\\','jason',0,NULL,NULL,NULL,NULL,'3',NULL,0,0,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);";
        str = str.replace(" ", "");
        System.out.println(str);
        String[] strs = str.split("VALUES");
        String fs = strs[0];
        String ss = strs[1];
        int s = fs.indexOf("(");
        int e = fs.indexOf(")");
        if (s >= 0 && e > 0) {
            String fstr = fs.substring(s + 1, e).toLowerCase();
            String[] fstrs = fstr.split(",");
            for (int i = 0; i < fstrs.length; i++) {
                String col = fstrs[i];
                col = col.replace("\"", "");
                System.out.println(col);
            }
        }


        List<String> list = parserLine(str);
        System.out.println(list.size());
    }

}