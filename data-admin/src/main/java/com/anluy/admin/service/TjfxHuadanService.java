package com.anluy.admin.service;

import java.io.IOException;

/**
 * 功能说明：话单分析
 * <p>
 * Created by hc.zeng on 2018/5/26.
 */
public interface TjfxHuadanService  extends TjfxService{

    /**
     * 分析通话记录
     * @param hdid
     * @param token
     * @return
     */
    Object analyzeThjl(String hdid,String token);
    /**
     * 分析通话对端记录
     * @param hdid
     * @param token
     * @return
     */
    Object analyzeDdxx(String hdid,String token);

    /**
     * 通话时长统计
     * @param hdId
     * @param token
     * @return
     */
    Object analyzeThsc(String hdId, String token) ;

}
