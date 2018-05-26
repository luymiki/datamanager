package com.anluy.admin.interceptor;

import com.alibaba.fastjson.JSON;
import com.anluy.commons.web.Result;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    private CacheManager cacheManager;

    //在请求处理之前进行调用（Controller方法调用之前
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String token = httpServletRequest.getHeader(AUTHORIZATION);
        if (StringUtils.isBlank(token)) {
            token = httpServletRequest.getParameter(AUTHORIZATION);
        }
        if (StringUtils.isNotBlank(token)) {

            EhCacheCache cache = (EhCacheCache)  cacheManager.getCache(CACHE_NAME);
            Object authCache = cache.get(token);
            if (authCache != null) {
                cache.put(token,token);
                return true;
            }
        }
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(Result.error(HttpStatus.UNAUTHORIZED.value(),"登录验证失败").setPath(httpServletRequest.getRequestURI())));
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
