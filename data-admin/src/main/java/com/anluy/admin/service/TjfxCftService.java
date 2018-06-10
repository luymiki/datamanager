package com.anluy.admin.service;

import com.anluy.commons.service.BaseService;

import java.io.IOException;

/**
 * 功能说明：财付通统计分析功能
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
public interface TjfxCftService extends TjfxService{
    /**
     * 分析交易流水数据
     * @param cftId
     * @param token
     * @return
     * @throws IOException
     */
    Object analyzeJyls(String cftId,String token) throws IOException;

    /**
     * 分析交易对手数据
     * @param cftId
     * @param token
     * @return
     * @throws IOException
     */
    Object analyzeJyds(String cftId,String cftzh, String token) throws IOException;

    /**
     * 分析交易金额数据
     * @param cftId
     * @param token
     * @return
     * @throws IOException
     */
    Object analyzeJyje(String cftId,String token) throws IOException;


}