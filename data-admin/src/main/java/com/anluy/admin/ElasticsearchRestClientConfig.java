package com.anluy.admin;

import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/17.
 */
@Component
@ConfigurationProperties(prefix = "elasticsearch.node")
public class ElasticsearchRestClientConfig {
    private String host;
    private String hosts;
    private int port;
    private String userName;
    private String password;

    @Bean
    public ElasticsearchRestClient getElasticsearchRestClient() {
        if(StringUtils.isNotBlank(hosts)){
            List<HttpHost> httpHosts = new ArrayList<>();
            for (String hp : hosts.split(",")) {
                String[] pp = hp.split(":");
                httpHosts.add(new HttpHost(pp[0], Integer.valueOf(pp[1]), "http"));
            }
            return new ElasticsearchRestClient(httpHosts.toArray(new HttpHost[httpHosts.size()]), userName, password);
        }else {
            return new ElasticsearchRestClient(host,port, userName, password);
        }
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
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
