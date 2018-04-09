package com.anluy.admin.service;

import com.anluy.admin.entity.Suspicious;
import com.anluy.commons.service.BaseService;

import java.io.IOException;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
public interface SuspiciousService extends BaseService<String,Suspicious> {
    /**
     * 分析提取数据
     * @param suspId
     */
    void analyze(String suspId) throws IOException;
}
