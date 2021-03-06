package com.anluy.admin.service;

import java.io.IOException;

/**
 * 功能说明：支付宝统计分析功能
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
public interface TjfxZfbService extends TjfxJylsService{

    /**
     * 整合数据
     * @param userId
     * @param token
     * @return
     * @throws IOException
     */
    Object integrated(String userId, String xcbh, String token) throws Exception;

    /**
     * 分析交易流水数据
     * @param userId
     * @param token
     * @return
     * @throws IOException
     */
    Object analyzeJyls(String userId, String xcbh, String jyjeRange,String dsId, String zcType,String token) throws IOException;

    /**
     * 分析交易对手数据
     * @param token
     * @return
     * @throws IOException
     */
    Object analyzeJyds(String userId, String xcbh, String zcType, String token) throws IOException;

    /**
     * 分析交易金额数据
     * @param userId
     * @param token
     * @return
     * @throws IOException
     */
    Object analyzeJyje(String userId, String xcbh,String dsId,  String zcType,String token) throws IOException;

    /**
     * 分析金额被100整除的流水数据
     * @param userId
     * @param token
     * @return
     * @throws IOException
     */
    Object analyzeJyjeZc100(String userId, String xcbh,String dsId, String token) throws IOException;


}
