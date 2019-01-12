package com.anluy.admin;

import com.anluy.admin.interceptor.AuthorizedInterceptor;
import com.anluy.admin.utils.IPAddrUtil;
import com.anluy.admin.utils.PhoneAddrUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.io.InputStream;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/4/24.
 */
@Configuration
public class WebConfig {
    @Resource
    private AuthorizedInterceptor authorizedInterceptor;

    @Resource
    private JdbcTemplate jdbcTemplate;
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
                                "/api/admin/websocketServer/**",
                                "/api/admin/websocket/**",
                                "/error",
                                "/login",
                                "/swagger-ui.html",
                                "/v2/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**");
            }
        };
    }

    @Bean
    public IPAddrUtil iPAddrUtil() {
        InputStream inputStream = WebConfig.class.getResourceAsStream("/ipipfree.ipdb");
        return new IPAddrUtil(inputStream);
    }
    @Bean
    public PhoneAddrUtil phoneAddrUtil( ) {
        return new PhoneAddrUtil(jdbcTemplate);
    }
}
