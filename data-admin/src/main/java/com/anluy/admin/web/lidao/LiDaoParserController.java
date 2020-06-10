package com.anluy.admin.web.lidao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.utils.MD5;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.web.Result;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 功能说明：离岛数据文件导入操作
 * <p>
 * Created by hc.zeng on 2019/11/30.
 */
@RestController
@RequestMapping("/api/admin/lidao")
@Api(value = "/api/admin/lidao", description = "离岛数据文件导入操作")
public class LiDaoParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LiDaoParserController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    /**
     * 离岛数据解析保存文件
     *
     * @return
     */
    @ApiOperation(value = "解析保存文件", response = Result.class)
    @RequestMapping(value = "/parser", method = RequestMethod.POST)
    public Object parser(HttpServletRequest request, @RequestParam String path, @RequestParam String type,
                         @RequestParam(required = false) String encoding,
                         @RequestParam(required = false) String indexName,
                         @RequestParam(required = false) String batchSize) {
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
            LiDaoParse parse = LiDaoParseFctory.getParse(type);
            executor = new ThreadPoolExecutor(5, 10, 60000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1024), new ThreadPoolExecutor.CallerRunsPolicy());
            System.out.println("开始时间：" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

            for (File f : fileList) {
                int ri = 0;
                if(StringUtils.isBlank(indexName)){
                    indexName = parse.getIndex(f.getName());
                }
                if(StringUtils.isBlank(encoding)){
                    encoding = com.anluy.admin.utils.FileUtils.detectorCharset(f);
                }
                if (StringUtils.isBlank(indexName)) {
                    continue;
                }
                LOGGER.info("解析文件：" + f.getAbsolutePath());
                LOGGER.info("文件编码：" + encoding);
                LineIterator lineIterator = new LineIterator(new BufferedReader(new InputStreamReader(FileUtils.openInputStream(f), Charsets.toCharset(encoding)), 10 * 1024 * 1024));
                List<Map> data = new ArrayList<>();
                while (lineIterator.hasNext()) {
                    String line = lineIterator.next();
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

    private interface LiDaoParse {
        JSONObject _parse(String type, String line, String fileName);

        String getIndex(String fileName);

        default JSONObject parse(String type, String line, String fileName, ElasticsearchRestClient elasticsearchRestClient) {
            JSONObject d = _parse(type, line, fileName);
            if (d == null || d.isEmpty()) {
                return null;
            }
            String id = MD5.encode(d.toJSONString());
            d.put("id", id);
            d.put("_id", id);
            d.put("create_time", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            return d;
        }
    }

    private static class LiDaoParseFctory {
        public static LiDaoParse getParse(String type) {
            switch (type) {
                case "1": {
                    return new Type1LiDaoParse();
                }
                case "2": {
                    return new Type2LiDaoParse();
                }
                case "3": {
                    return new Type3LiDaoParse();
                }
                case "4": {
                    return new Type4LiDaoParse();
                }
            }
            return null;
        }
    }

    private static class Type1LiDaoParse implements LiDaoParse {

        @Override
        public JSONObject _parse(String type, String line, String fileName) {
            String[] items = line.split(",");
            switch (fileName) {
                case "foreignmigrate.txt":
                    return foreignmigrate(type, items, fileName);
                case "foreignstay.txt":
                    return foreignstay(type, items, fileName);
                case "immigrations.txt":
                    return immigrations(type, items, fileName);
                case "nonsoliders.txt":
                    return nonsoliders(type, items, fileName);
                case "passengerinfo.txt":
                    return passengerinfo(type, items, fileName);
                case "sol.txt":
                    return sol(type, items, fileName);
                case "students.txt":
                    return students(type, items, fileName);
            }
            return null;
        }

        private JSONObject mapping(String[] items, Map<String, Integer> mapping) {
            JSONObject jsonObject = new JSONObject();
            mapping.forEach((it, inx) -> jsonObject.put(it, quotaItem(items[inx])));
            return jsonObject;
        }

        private JSONObject foreignmigrate(String type, String[] items, String fileName) {
            if (items.length < 26) {
                return null;
            }
            return mapping(items, foreignmigrateMap);
        }

        private static final Map<String, Integer> foreignmigrateMap = new HashMap<>();

        static {
            foreignmigrateMap.put("SerialNo", 0);
            foreignmigrateMap.put("ApplyDate", 1);
            foreignmigrateMap.put("ResidentIdNo", 3);
            foreignmigrateMap.put("EnglishName", 4);
            foreignmigrateMap.put("Gender", 5);
            foreignmigrateMap.put("BirthDate", 6);
            foreignmigrateMap.put("NationName", 8);
            foreignmigrateMap.put("Address", 9);
            foreignmigrateMap.put("NewAddress", 10);
            foreignmigrateMap.put("LastUpdateTime", 23);
            foreignmigrateMap.put("ImportTime", 25);
        }

        private JSONObject foreignstay(String type, String[] items, String fileName) {
            if (items.length < 52) {
                return null;
            }
            return mapping(items, foreignstayMap);
        }

        private static final Map<String, Integer> foreignstayMap = new HashMap<>();

        static {
            foreignstayMap.put("SerialNo", 0);
            foreignstayMap.put("PassportNo", 1);
            foreignstayMap.put("EnglishName", 2);
            foreignstayMap.put("BirthDate", 3);
            foreignstayMap.put("Gender", 4);
            foreignstayMap.put("Nation", 5);
            foreignstayMap.put("ResidenceId", 6);
            foreignstayMap.put("ExpiryDate", 7);
            foreignstayMap.put("ImportTime", 10);
            foreignstayMap.put("MainUserId", 12);
            foreignstayMap.put("LastUpdateTime", 15);
            foreignstayMap.put("ChineseName", 16);
            foreignstayMap.put("EdCardNo", 24);
            foreignstayMap.put("EntryDate", 25);
            foreignstayMap.put("EntrySerialNo", 26);
            foreignstayMap.put("EntryFlightNo", 27);
            foreignstayMap.put("Addr", 33);
            foreignstayMap.put("NationalityChangeMark", 41);
            foreignstayMap.put("Remark", 44);
            foreignstayMap.put("ExitKeyinUserId2", 51);
        }

        private JSONObject immigrations(String type, String[] items, String fileName) {
            if (items.length < 32) {
                return null;
            }
            return mapping(items, immigrationsMap);
        }

        private static final Map<String, Integer> immigrationsMap = new HashMap<>();

        static {
            immigrationsMap.put("PassengerId", 0);
            immigrationsMap.put("ImmigrateDate", 1);
            immigrationsMap.put("ProcessDate", 5);
            immigrationsMap.put("FlightNo", 8);
            immigrationsMap.put("InspectorId", 10);
            immigrationsMap.put("ImmigrateTime", 13);
            immigrationsMap.put("Nation", 14);
            immigrationsMap.put("PermitNo", 15);
            immigrationsMap.put("PassportNo", 16);
            immigrationsMap.put("EdCardNo", 17);
            immigrationsMap.put("ImportTime", 26);
            immigrationsMap.put("LastUpdateTime", 31);
        }

        private JSONObject nonsoliders(String type, String[] items, String fileName) {
            if (items.length < 18) {
                return null;
            }
            return mapping(items, nonsolidersMap);
        }

        private static final Map<String, Integer> nonsolidersMap = new HashMap<>();

        static {
            nonsolidersMap.put("PersonId", 0);
            nonsolidersMap.put("ChineseName", 3);
            nonsolidersMap.put("BirthDate", 4);
            nonsolidersMap.put("ReportDate", 5);
            nonsolidersMap.put("TransactionDate", 6);
            nonsolidersMap.put("TransactionTime", 7);
            nonsolidersMap.put("Reserved", 10);
            nonsolidersMap.put("ImportTime", 12);
            nonsolidersMap.put("LastUpdateTime", 17);
        }

        private JSONObject passengerinfo(String type, String[] items, String fileName) {
            if (items.length < 23) {
                return null;
            }
            return mapping(items, passengerinfoMap);
        }

        private static final Map<String, Integer> passengerinfoMap = new HashMap<>();

        static {
            passengerinfoMap.put("PassengerId", 0);
            passengerinfoMap.put("EnglishName", 2);
            passengerinfoMap.put("ChineseName", 3);
            passengerinfoMap.put("BirthPlace2", 6);
            passengerinfoMap.put("Gender", 7);
            passengerinfoMap.put("BirthDate", 8);
            passengerinfoMap.put("Nation", 9);
            passengerinfoMap.put("LastUpdateTime", 17);
            passengerinfoMap.put("SortEnglishName", 22);
        }

        private JSONObject sol(String type, String[] items, String fileName) {
            if (items.length < 20) {
                return null;
            }
            return mapping(items, solMap);
        }

        private static final Map<String, Integer> solMap = new HashMap<>();

        static {
            solMap.put("PersonId", 0);
            solMap.put("BirthDate", 2);
            solMap.put("ChineseName", 3);
            solMap.put("Addr1", 6);
            solMap.put("Addr2", 7);
            solMap.put("Addr3", 8);
            solMap.put("Addr4", 9);
            solMap.put("ImportTime", 19);
        }

        private JSONObject students(String type, String[] items, String fileName) {
            if (items.length < 20) {
                return null;
            }
            return mapping(items, studentsMap);
        }

        private static final Map<String, Integer> studentsMap = new HashMap<>();

        static {
            studentsMap.put("PersonId", 0);
            studentsMap.put("ChineseName", 2);
            studentsMap.put("BirthDate", 3);
            studentsMap.put("Addr1", 7);
            studentsMap.put("Addr2", 8);
            studentsMap.put("Addr3", 9);
            studentsMap.put("Addr4", 10);
            studentsMap.put("Education", 11);
            studentsMap.put("BeginDate", 12);
            studentsMap.put("EndDate", 13);
            studentsMap.put("ApprovalDoc", 14);
            studentsMap.put("LastUpdateTime", 27);
        }

        @Override
        public String getIndex(String fileName) {
            switch (fileName) {
                case "foreignmigrate.txt":
                    return "ymj_foreignmigrate";
                case "foreignstay.txt":
                    return "ymj_foreignstay";
                case "immigrations.txt":
                    return "ymj_immigrations";
                case "nonsoliders.txt":
                    return "ymj_nonsoliders";
                case "passengerinfo.txt":
                    return "ymj_passengerinfo";
                case "sol.txt":
                    return "ymj_sol";
                case "students.txt":
                    return "ymj_students";
            }
            return null;
        }

    }

    private static class Type2LiDaoParse implements LiDaoParse {
        @Override
        public JSONObject _parse(String type, String line, String fileName) {
            String[] items = line.split("\t");
            if (items.length < 11) {
                return null;
            }
            quota(items);
            String crjlx = items[9];
            String cfjc = items[7];
            String mdjc = items[4];
            if ("入".equals(crjlx)) {
                cfjc = items[4];
                mdjc = items[7];
            }
            JSONObject o = lidaoinfo(items[2], items[10], items[6], null, null,
                    null, items[7], cfjc, mdjc, null,
                    items[10], items[10], items[1], null, crjlx,
                    items[0], items[3], type, fileName);

            return o;
        }

        @Override
        public String getIndex(String fileName) {
            return "lidaoinfo2";
        }
    }

    private static class Type3LiDaoParse extends Type4LiDaoParse {
        @Override
        public String getIndex(String fileName) {
            return "lidaoinfo3";
        }
    }

    private static class Type4LiDaoParse implements LiDaoParse {
        @Override
        public JSONObject _parse(String type, String line, String fileName) {
            String[] items = line.split("\t");
            if (items.length < 38) {
                return null;
            }
            String crjlx = "入";
            String crjka = items[16];
            String zzjc = items[17];
            if (StringUtils.isNotBlank(zzjc) && !"null".equals(zzjc) && TWJC.contains(zzjc)) {
                crjlx = "入";
                crjka = zzjc;
            } else if (StringUtils.isNotBlank(zzjc) && !"null".equals(zzjc) && !TWJC.contains(zzjc)) {
                crjlx = "出";
                crjka = null;
            } else if (TWJC.contains(crjka)) {
                crjlx = "入";
            } else {
                crjlx = "出";
            }

            JSONObject o = lidaoinfo(items[0], items[1], items[2], items[7], items[8],
                    items[9], crjka, items[15], items[16], items[22],
                    items[28], items[29], items[4] + " " + items[5], items[37], crjlx,
                    null, null, type, fileName);

            return o;
        }

        @Override
        public String getIndex(String fileName) {
            return "lidaoinfo4";
        }
    }

    private static final Set<String> TWJC = ImmutableSet.copyOf("KHH,TPE,TSA,CMJ,CYI,GNI,HCN,HSZ,HUN,KNH,KYD,LHN,LZN,MZG,PIF,RMQ,SMT,TNN,TTT,TXG,WOT".split(","));

    private static JSONObject lidaoinfo(Object hbh, Object cjsj, Object hzh, Object xb, Object csrj,
                                        Object gj, Object crjka, Object cfjc, Object mdjc, Object zjdqsj,
                                        Object cfsj, Object lgsj, Object xm, Object ddsj, Object crjlx,
                                        Object sfzh, Object crzh, Object sjly, Object wjm) {
        JSONObject obj = new JSONObject();
        obj.put("hbh", hbh);
        obj.put("cjsj", cjsj);
        obj.put("hzh", hzh);
        obj.put("sfzh", sfzh);
        obj.put("crzh", crzh);
        obj.put("xb", xb);
        obj.put("csrj", csrj);
        obj.put("gj", gj);
        obj.put("crjka", crjka);
        obj.put("cfjc", cfjc);
        obj.put("mdjc", mdjc);
        obj.put("zjdqsj", zjdqsj);
        obj.put("cfsj", cfsj);
        obj.put("lgsj", lgsj);
        obj.put("xm", xm);
        obj.put("ddsj", ddsj);
        obj.put("sjly", sjly);
        obj.put("wjm", wjm);
        obj.put("crjlx", crjlx);
        return obj;
    }

    private static void quota(String[] items) {
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            if (StringUtils.isNotBlank(item) && item.startsWith("\"") && item.endsWith("\"")) {
                items[i] = item.substring(1, item.length() - 1);
            }
        }
    }

    private static String quotaItem(String item) {
        if (StringUtils.isNotBlank(item) && item.startsWith("\"") && item.endsWith("\"")) {
            return item.substring(1, item.length() - 1);
        }
        return item;
    }
}