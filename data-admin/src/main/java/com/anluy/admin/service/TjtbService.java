package com.anluy.admin.service;

import java.text.ParseException;

/**
 * 功能说明：统计图表功能
 * <p>
 * Created by hc.zeng on 2018/5/27.
 */
public interface TjtbService extends TjfxService {

    /**
     * 分析系统数据
     * @param token
     * @return
     */
    Object analyzeCjsj(String token) throws Exception;
}
