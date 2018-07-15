package com.anluy.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.EqaConfig;
import com.anluy.admin.service.JhFxService;
import com.anluy.admin.utils.HTTPUtils;
import com.anluy.admin.utils.graph.GraphContainer;
import com.anluy.admin.utils.graph.Link;
import com.anluy.admin.utils.graph.Node;
import com.anluy.commons.dao.BaseDAO;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.service.BaseServiceImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * 功能说明：集合分析
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
@Service
@Transactional
public class JhfxServiceImpl extends BaseServiceImpl implements JhFxService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JhfxServiceImpl.class);

    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private EqaConfig eqaConfig;

    private String queryDsl;

    @Override
    public BaseDAO getBaseDAO() {
        return null;
    }

    @Override
    public ElasticsearchRestClient getElasticsearchRestClient() {
        return elasticsearchRestClient;
    }

    @Override
    public String getIndexName() {
        return "suspicious";
    }

    /**
     * 分析提取数据
     *
     * @param paramsArr
     * @param fieldArr
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public Object analyze(String paramsArr, String fieldArr, String token) throws IOException {
        JSONArray queryItem = (JSONArray) JSON.parse(paramsArr);
        JSONArray fieldArray = (JSONArray) JSON.parse(fieldArr);

//        DefaultDirectedGraph<Node, Link> graph = new DefaultDirectedGraph<Node, Link>(Link.class);
//        GraphContainer graphContainer = new GraphContainer();

        Map<Object,Integer> allMap = new HashMap<>();

        for (int i = 0; i < queryItem.size(); i++) {
            JSONObject jo = (JSONObject) queryItem.get(i);
            String field = fieldArray.getString(i);
            JSONArray dataList = new JSONArray();
            queryByDsl(eqaConfig.getQueryUrl(),JSON.toJSONString(jo), token, dataList);

            Set<Object> dataSet = new HashSet<>();
            for (int j = 0; j < dataList.size(); j++) {
                JSONObject job = (JSONObject) dataList.get(j);
                Object data = job.get(field);
                if (data instanceof List) {
                    List dl = (List) data;
                    for (Object o : dl) {
                        if (!dataSet.contains(o)) {
                            dataSet.add(o);
                        }
                    }
                } else {
                    if (!dataSet.contains(data)) {
                        dataSet.add(data);
                    }
                }
            }
            for (Object data : dataSet) {
                if(allMap.containsKey(data)){
                    allMap.put(data,allMap.get(data)+1);
                }else {
                    allMap.put(data,1);
                }
            }
            //System.out.println(dataSet);
        }
        //LOGGER.debug(dataList);
        //System.out.println(dataList);
        int size = fieldArray.size();
        List<Object> resultList = new ArrayList<>();
        allMap.forEach((data,integer)->{
            if(integer>= size){
                resultList.add(data);
            }
        });
        return resultList;
    }

}
