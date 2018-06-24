package com.anluy.admin;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/6/14.
 */
@Configuration
@EnableCaching
public class ECacheConfig {

    @Bean
    public CacheManager cacheManager(){
        CacheManager cacheManager = new EhCacheCacheManager();
        return cacheManager;
    }
}
