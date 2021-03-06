package com.anluy.admin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
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
    private String backDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getBackDir() {
        return backDir;
    }

    public void setBackDir(String backDir) {
        this.backDir = backDir;
    }
}
