package com.anluy.admin.eqa;

import com.anluy.admin.eqa.interceptor.AuthorizedInterceptor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

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
                                "/swagger-ui.html",
                                "/v2/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**");
            }
        };
    }

}
