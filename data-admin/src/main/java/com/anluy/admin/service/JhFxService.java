package com.anluy.admin.service;

import com.anluy.admin.entity.Suspicious;
import com.anluy.commons.service.BaseService;

import java.io.IOException;

/**
 * 功能说明：集合分析
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
public interface JhFxService extends TjfxService {
    /**
     * 分析数据
     * @param paramsArr
     */
    Object analyze(String paramsArr,String fieldArr, String token) throws IOException;


}
