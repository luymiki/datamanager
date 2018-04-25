package com.anluy.admin;

import com.anluy.admin.interceptor.AuthorizedInterceptor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/4/24.
 */
@Configuration
@EnableCaching
public class WebConfig {
    @Resource
    private AuthorizedInterceptor authorizedInterceptor;
    /**
     * 实例化WebMvcConfigurer接口
     *
     * @return
     */
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * 添加拦截器
             * @param registry
             */
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(authorizedInterceptor).addPathPatterns("/**")
                        .excludePathPatterns(
                                "/api/admin/authorization/**",
                                "/login",
                                "/swagger-ui.html",
                                "/v2/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**");
            }
        };
    }

    @Bean
    public CacheManager cacheManager(){
        CacheManager cacheManager = new EhCacheCacheManager();
        return cacheManager;
    }

}
