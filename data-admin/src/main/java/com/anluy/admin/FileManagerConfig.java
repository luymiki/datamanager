package com.anluy.admin;

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
}
