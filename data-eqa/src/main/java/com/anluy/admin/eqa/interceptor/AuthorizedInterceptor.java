package com.anluy.admin.eqa.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anluy.commons.web.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能说明：登录拦截器
 * <p>
 * Created by hc.zeng on 2018/4/24.UNAUTHORIZED
 */
@Component
public class AuthorizedInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizedInterceptor.class);

    public final static String AUTHORIZATION = "Authorization";
    public final static String CACHE_NAME = "Authorization-Cache";

    @Resource
    private Environment environment;

    //在请求处理之前进行调用（Controller方法调用之前
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        try {
            String auth = httpServletRequest.getHeader(AUTHORIZATION);
            if (StringUtils.isBlank(auth)) {
                auth = httpServletRequest.getParameter(AUTHORIZATION);
            }
            if (StringUtils.isNotBlank(auth)) {

                HttpClient httpClient = HttpClients.createDefault();
                ;

                // 设置超时时间
                RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();

                HttpPost post = new HttpPost(environment.getProperty("authorization.url"));
                post.setConfig(requestConfig);
                // 构造消息头
                post.setHeader("Content-type", "application/x-www-form-urlencoded; charset=utf-8");
                post.setHeader("Connection", "Close");
                post.setHeader("Authorization", auth);

                // 构建消息实体
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                nvps.add(new BasicNameValuePair("authorization", auth));
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, Charset.forName("utf-8"));
                entity.setContentEncoding("UTF-8");
                post.setEntity(entity);

                HttpResponse response = httpClient.execute(post);
                if (response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
                    HttpEntity reEntity = response.getEntity();
                    //返回值处理
                    BufferedReader br = new BufferedReader(new InputStreamReader(reEntity.getContent(), "utf-8"));
                    String s = null;
                    StringBuffer sb = new StringBuffer();
                    while ((s = br.readLine()) != null) {
                        sb.append(s);
                    }
                    JSONObject jo = (JSONObject) JSON.parse(sb.toString());
                    if (jo != null && jo.getInteger("status") == 200) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("登录验证失败！", e);
        }
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(Result.error(HttpStatus.UNAUTHORIZED.value(), "登录验证失败")));
        return false;
    }

    //请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        //LOGGER.debug("");
    }

    //在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        //
    }


}
