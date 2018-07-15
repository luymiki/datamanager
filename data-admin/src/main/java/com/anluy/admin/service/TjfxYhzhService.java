package com.anluy.admin.service;

import java.io.IOException;

/**
 * 功能说明：银行流水统计分析功能
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
public interface TjfxYhzhService extends TjfxJylsService{


    /**
     * 分析交易流水数据
     * @param token
     * @return
     * @throws IOException
     */
    Object analyzeJyls(String ssyh,String kh,String zh, String jyjeRange, String dsId, String zcType, String token) throws IOException;

    /**
     * 分析交易对手数据
     * @param token
     * @return
     * @throws IOException
     */
    Object analyzeJyds(String ssyh,String kh,String zh, String zcType, String token) throws IOException;

    /**
     * 分析交易金额数据
     * @param token
     * @return
     * @throws IOException
     */
    Object analyzeJyje(String ssyh,String kh,String zh, String dsId, String zcType, String token) throws IOException;

    /**
     * 分析交易金额能被100整除的数据，
     * @param token
     * @return
     * @throws IOException
     */
    Object analyzeZc100(String ssyh,String kh,String zh, String dsId, String token) throws IOException;


}
