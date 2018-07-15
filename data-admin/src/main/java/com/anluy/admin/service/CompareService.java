package com.anluy.admin.service;

import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 功能说明：比对服务
 * <p>
 * Created by hc.zeng on 2018/7/2.
 */
@Service
public class CompareService {

    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    public void compare(){

    }

}
