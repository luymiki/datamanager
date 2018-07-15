package com.anluy.admin.utils;

import org.springframework.context.ApplicationContext;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/7/2.
 */
public class SpringUtils {

    private static ApplicationContext applicationContext;

    private SpringUtils() {
    }
    public static void setApplicationContext(ApplicationContext applicationContext){
        SpringUtils.applicationContext = applicationContext;
    }
    public static ApplicationContext getApplicationContext(){
        return SpringUtils.applicationContext;
    }
}
