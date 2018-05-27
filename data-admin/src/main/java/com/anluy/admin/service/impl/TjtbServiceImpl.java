package com.anluy.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.EqaConfig;
import com.anluy.admin.service.TjtbService;
import com.anluy.commons.dao.BaseDAO;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.service.BaseServiceImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/5/27.
 */
@Service
public class TjtbServiceImpl extends BaseServiceImpl implements TjtbService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TjtbServiceImpl.class);

    private static final String START_TIME = "2018-02-01 00:00:00";

    @Resource
    private EqaConfig eqaConfig;

    private String queryDsl;

    public TjtbServiceImpl() {
        //dsl查询语句
        if (StringUtils.isBlank(queryDsl)) {
            try {
                queryDsl = IOUtils.toString(TjfxCftServiceImpl.class.getResourceAsStream("/dsl/eqa-query.json"));
            } catch (IOException e) {
                LOGGER.error("DSL文件加载错误", e);
            }
        }
    }

    /**
     * 分析系统数据
     *
     * @param token
     * @return
     */
    @Override
    public Object analyzeCjsj(String token) throws Exception {
        JSONObject dslJson = (JSONObject) JSON.parse(queryDsl);
        JSONArray aggsJSONArray = dslJson.getJSONArray("aggs");
        aggsJSONArray.clear();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = sdf.parse(START_TIME);//开始时间
        Date endTime = new Date();//当前时间

        List<String[]> rangeList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        while (true) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int minDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
            rangeList.add(new String[]{addDateTimeStr(year, month, minDay, true), addDateTimeStr(year, month, maxDay, false)});
            //滚到下一个月
            calendar.add(Calendar.MONTH, 1);
            startTime = calendar.getTime();
            if (endTime.getTime() < startTime.getTime()) {
                break;
            }
        }
        List<Map> values = new ArrayList<>();
        for (String[] range : rangeList) {
            Map<String, String> map = new HashMap<>();
            map.put("from", range[0]);
            map.put("to", range[1]);
            values.add(map);
        }
        JSONObject max = new JSONObject();
        max.put("groupName", "group_createTime");
        max.put("field", "create_time");
        max.put("values", values);
        max.put("aggsType", 8);
        aggsJSONArray.add(max);
        JSONObject aggsObj = aggs(eqaConfig.getAggsUrl(), JSON.toJSONString(dslJson), token);
        aggsObj.put("rangeList",rangeList);
        return aggsObj;
    }

    private String addDateTimeStr(int year, int month, int day, boolean start) {
        //year+"-"+(month+1)+"-"+minMonth+" 00:00:00"
        StringBuffer sb = new StringBuffer();
        sb.append(year).append("-");
        if (month < 9) {
            sb.append("0").append(month + 1);
        } else {
            sb.append(month + 1);
        }
        sb.append("-");
        if (day < 10) {
            sb.append("0").append(day);
        } else {
            sb.append(day);
        }

        if (start) {
            sb.append(" 00:00:00");
        } else {
            sb.append(" 23:59:59");
        }
        return sb.toString();
    }

    @Override
    public BaseDAO getBaseDAO() {
        return null;
    }

    @Override
    public ElasticsearchRestClient getElasticsearchRestClient() {
        return null;
    }

    @Override
    public String getIndexName() {
        return null;
    }


}
