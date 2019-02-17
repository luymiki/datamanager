package com.anluy.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2019/1/22.
 */
@EnableScheduling
@Component
@ConfigurationProperties("file.manager")
public class BackAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackAction.class);
    private String backDir;
    private BufferedWriter bufferedWriter = null;
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24, initialDelay = 6000)
    public void back() {
        try {
            LOGGER.info("开始备份数据");
            JSONObject map = elasticsearchRestClient.get("/_mapping");
            File mf = new File(backDir + "/_mapping.back");
            if (!mf.exists()) {
                mf.createNewFile();
            }
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mf), "utf-8"));
            bufferedWriter.write(JSON.toJSONString(map, SerializerFeature.WriteMapNullValue));
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
            bufferedWriter = null;
            map.forEach((indexName, mappings) -> {
                try {
                    File file = new File(backDir + "/" + indexName + ".back");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
                    elasticsearchRestClient.scroll("{\"size\":1000}", null, new ElasticsearchRestClient.TimeWindowCallBack() {
                        @Override
                        public void process(List<Map> var1) {
                            save(var1);
                        }
                    }, indexName, null, null);
                    LOGGER.info("[" + indexName + "] 备份完成");
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                } finally {
                    if (bufferedWriter != null) {
                        try {
                            bufferedWriter.close();
                        } catch (IOException e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                }
            });
            LOGGER.info("备份数据完成");
        } catch (IOException e) {
            LOGGER.info("备份数据失败：" + e.getMessage(), e);
        }
    }

    private void save(List<Map> var1) {
        if (var1 != null && !var1.isEmpty()) {
            try {
                for (Map map : var1) {
                    bufferedWriter.write(JSON.toJSONString(map, SerializerFeature.WriteMapNullValue));
                    bufferedWriter.newLine();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }


    public String getBackDir() {
        return backDir;
    }

    public void setBackDir(String backDir) {
        this.backDir = backDir;
    }
}
