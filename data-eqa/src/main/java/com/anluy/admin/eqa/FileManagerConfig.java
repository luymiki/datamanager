package com.anluy.admin.eqa;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/16.
 */
@Component
@ConfigurationProperties(prefix = "file.manager")
public class FileManagerConfig {

    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        //// 设置文件大小限制 ,超了，页面会抛出异常信息，这时候就需要进行异常信息的处理了;
//        factory.setMaxFileSize("256MB"); //KB,MB
//        /// 设置总上传数据总大小
//        //factory.setMaxRequestSize("256KB");
//        return factory.createMultipartConfig();
//    }
//    @Bean
//    public MultipartResolver multipartResolver() {
//        return new CommonsMultipartResolver();
//    }
}
