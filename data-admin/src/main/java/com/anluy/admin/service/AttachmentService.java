package com.anluy.admin.service;

import com.anluy.admin.entity.Attachment;
import com.anluy.commons.service.BaseService;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/19.
 */
public interface AttachmentService extends BaseService<String,Attachment> {
    Attachment getMd5(String md5);
}
