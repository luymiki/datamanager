package com.anluy.admin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/16.
 */
@Component
@ConfigurationProperties(prefix = "eqa")
public class EqaConfig {

    private String queryUrl;
    private String aggsUrl;

    public String getQueryUrl() {
        return queryUrl;
    }

    public void setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
    }

    public String getAggsUrl() {
        return aggsUrl;
    }

    public void setAggsUrl(String aggsUrl) {
        this.aggsUrl = aggsUrl;
    }
}
