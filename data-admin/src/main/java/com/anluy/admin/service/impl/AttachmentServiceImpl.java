package com.anluy.admin.service.impl;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.mapper.AttachmentMapper;
import com.anluy.admin.service.AttachmentService;
import com.anluy.commons.dao.BaseDAO;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.service.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/19.
 */
@Service
public class AttachmentServiceImpl extends BaseServiceImpl<String,Attachment> implements AttachmentService {
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Override
    public Object formater(String field, Object value) {
        switch (field){
            case "tags":{
                if(StringUtils.isNotBlank((String)value)){
                    return ((String)value).split(",");
                }
                break;
            }
            case "create_time":{
                if(value!=null){
                    return DateFormatUtils.format((Date) value,"yyyy-MM-dd HH:mm:ss");
                }
                break;
            }
            default:
                break;
        }
        return value;
    }

    @Override
    public BaseDAO getBaseDAO() {
        return attachmentMapper;
    }

    @Override
    public ElasticsearchRestClient getElasticsearchRestClient() {
        return elasticsearchRestClient;
    }

    @Override
    public String getIndexName() {
        return "attachment";
    }
}
