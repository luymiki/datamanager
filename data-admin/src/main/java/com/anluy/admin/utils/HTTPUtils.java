package com.anluy.admin.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/4/9.
 */
public class HTTPUtils {
    /**
     * Post方式 得到JSONObject
     *
     * @param paramsHashMap post参数
     * @param url
     * @param encoding 编码utf-8
     * @return
     */
    public static JSONObject getJSONObjectByPost(String url,Map<String, String> paramsHashMap,  String encoding) {
        //创建httpClient连接
        CloseableHttpClient httpClient = HttpClients.createDefault();

        JSONObject result = null;
        List<NameValuePair> nameValuePairArrayList = new ArrayList<NameValuePair>();
        // 将传过来的参数添加到List<NameValuePair>中
        if (paramsHashMap != null && !paramsHashMap.isEmpty()) {
            //遍历map
            for (Map.Entry<String, String> entry : paramsHashMap.entrySet()) {
                nameValuePairArrayList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        try {
            // 利用List<NameValuePair>生成Post请求的实体数据
            // UrlEncodedFormEntity 把输入数据编码成合适的内容
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairArrayList, encoding);
            HttpPost httpPost = new HttpPost(url);
            // 为HttpPost设置实体数据
            httpPost.setEntity(entity);
            // HttpClient 发送Post请求
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // CloseableHttpResponse
                HttpEntity httpEntity = httpResponse.getEntity();
                if (httpEntity != null) {
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"), 10 * 1024);
                        StringBuilder strBuilder = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            strBuilder.append(line);
                        }
                        // 用fastjson的JSON将返回json字符串转为json对象
                        result = JSON.parseObject(strBuilder.toString());
                    } catch (Exception e) {
                        throw new RuntimeException("请求失败",e);
                    } finally {
                        if (reader != null) {
                            try {
                                //关闭流
                                reader.close();
                            } catch (IOException e) {
                                throw new RuntimeException("关闭连接失败",e);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("请求失败",e);
        }
        return result;
    }

    public static JSONObject getJSONObjectByGet(String url,Map<String, String> paramsHashMap){
        JSONObject resultJsonObject=null;

        //创建httpClient连接
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            URIBuilder builder = new URIBuilder(url);
            if (paramsHashMap != null) {
                for (String key : paramsHashMap.keySet()) {
                    builder.addParameter(key, paramsHashMap.get(key));
                }
            }
            URI uri = builder.build();
            //利用URL生成一个HttpGet请求
            HttpGet httpGet=new HttpGet(uri);

            CloseableHttpResponse httpResponse=httpClient.execute(httpGet);
            //得到httpResponse的状态响应码
            if (httpResponse.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
                //得到httpResponse的实体数据
                HttpEntity httpEntity=httpResponse.getEntity();
                if (httpEntity!=null) {
                    BufferedReader reader=null;
                    try {
                        reader=new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"), 8*1024);
                        String line=null;
                        StringBuffer entityStringBuilder = new StringBuffer();
                        while ((line=reader.readLine())!=null) {
                            entityStringBuilder.append(line);
                        }
                        // 从HttpEntity中得到的json String数据转为json
                        String json=entityStringBuilder.toString();
                        resultJsonObject=JSON.parseObject(json);
                    } catch (Exception e) {
                        throw new RuntimeException("请求失败",e);
                    } finally {
                        if (reader != null) {
                            try {
                                //关闭流
                                reader.close();
                            } catch (IOException e) {
                                throw new RuntimeException("关闭连接失败",e);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("请求失败",e);
        }

        return resultJsonObject;
    }
}
