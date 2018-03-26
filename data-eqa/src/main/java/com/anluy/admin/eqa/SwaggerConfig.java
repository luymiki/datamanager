package com.anluy.admin.eqa;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    /**
     * api版本信息
     */
    public static final String VERSION = "1.0.0";

    /**
     * api的文本信息
     * @return
     */
    private static final ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Elasticsearch Query Analyze API")
                .description("数据管理系统数据查询分析服务接口文档")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .termsOfServiceUrl("")
                .version(VERSION)
                .contact(new Contact("Anluy","www.anluy.com", "376160680@qq.com"))
                .build();
    }

    /**
     * api扫描配置信息，
     * @return
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) // 注解了 ApiOperation的方法纳入扫描范围
                .build();
    }
}
