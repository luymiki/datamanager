package com.anluy.admin;

import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/17.
 */
@Component
@ConfigurationProperties(prefix = "elasticsearch.node")
public class ElasticsearchRestClientConfig {
    private String host;
    private int port;
    private String userName;
    private String password;

    @Bean
    public ElasticsearchRestClient getElasticsearchRestClient() {
        return new ElasticsearchRestClient(host, port,userName,password);
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
