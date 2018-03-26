package com.anluy.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/19.
 */
@Configuration
@MapperScan("com.anluy.admin.mapper")
public class MybatisConfig {
}
