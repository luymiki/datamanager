package com.anluy.admin.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.utils.HTTPUtils;
import com.anluy.commons.BaseEntity;
import com.anluy.commons.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/5/21.
 */
public interface TjfxService {

    default void queryByDsl(String url,String dsl, String token, JSONArray dataList) {
        if (StringUtils.isNotBlank(dsl)) {
            JSONObject resultJson = getByDsl(url,dsl, token);
            if (resultJson != null && resultJson.getJSONObject("data") != null && resultJson.getJSONObject("data").getJSONArray("data") != null) {
                JSONArray list = resultJson.getJSONObject("data").getJSONArray("data");
                if (list != null) {
                    dataList.addAll(list);
                }
            }
        }
    }

    /**
     * 查询es
     *
     * @param dsl
     * @return
     */
    default JSONObject getByDsl(String url,String dsl, String token) {
        Map params = new HashMap<>();
        params.put("pageNum", "1");
        params.put("pageSize", "10000");
        params.put("paramsStr", dsl);
        Header[] headers = new Header[]{new BasicHeader("Authorization", token)};
        return HTTPUtils.getJSONObjectByPost(url, params, headers, "utf-8");
    }
    default JSONObject aggs(String url,String dsl,String token){
        Map params = new HashMap<>();
        params.put("pageNum", "1");
        params.put("pageSize", "10000");
        params.put("paramsStr", dsl);
        Header[] headers = new Header[]{new BasicHeader("Authorization", token)};
        JSONObject resultJson = HTTPUtils.getJSONObjectByPost(url, params, headers, "utf-8");
        if (resultJson != null && resultJson.getJSONObject("data") != null && resultJson.getJSONObject("data").getJSONArray("data") != null) {
            return resultJson.getJSONObject("data").getJSONObject("aggs");
        }
        return null;
    }
}
