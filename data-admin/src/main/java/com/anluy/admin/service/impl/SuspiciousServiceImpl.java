package com.anluy.admin.service.impl;

import com.anluy.admin.entity.Suspicious;
import com.anluy.admin.mapper.SuspiciousMapper;
import com.anluy.admin.service.SuspiciousService;
import com.anluy.commons.dao.BaseDAO;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.service.BaseServiceImpl;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 功能说明：嫌疑人信息管理
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
@Service
@Transactional
public class SuspiciousServiceImpl extends BaseServiceImpl<String, Suspicious> implements SuspiciousService {

    @Resource
    private SuspiciousMapper suspiciousMapper;

    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    public Object formater(String field, Object value) {
        if (value != null) {
            switch (field) {
                case "create_time":
                case "modify_time": {
                    return DateFormatUtils.format((Date) value, "yyyy-MM-dd HH:mm:ss");
                }
            }
        }
        return value;
    }

    @Override
    public BaseDAO getBaseDAO() {
        return suspiciousMapper;
    }

    @Override
    public ElasticsearchRestClient getElasticsearchRestClient() {
        return elasticsearchRestClient;
    }

    @Override
    public String getIndexName() {
        return "suspicious";
    }
}
