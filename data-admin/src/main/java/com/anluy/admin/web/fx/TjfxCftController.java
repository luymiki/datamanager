package com.anluy.admin.web.fx;

import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.service.TjfxCftService;
import com.anluy.admin.web.AuthorizationController;
import com.anluy.commons.web.Result;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能说明：财付通流水分析
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/fx/cft")
@Api(value = "/api/admin/fx/cft", description = "财付通流水分析")
public class TjfxCftController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TjfxCftController.class);

    @Resource
    private TjfxCftService tjfxCftService;

    /**
     * 财付通流水整合接口
     *
     * @return
     */
    @ApiOperation(value = "财付通流水整合", response = Result.class)
    @RequestMapping(value = "/integrated",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="cftId",value = "财付通ID")})
    public Object integrated(HttpServletRequest request,String cftId) {
        try {
            if(StringUtils.isBlank(cftId) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"财付通ID为空"));
            }
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxCftService.integrated(cftId,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("整合成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("整合失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
    
    /**
     * 分析可疑人信息接口
     *
     * @return
     */
    @ApiOperation(value = "财付通流水统计", response = Result.class)
    @RequestMapping(value = "/jyls",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="cftId",value = "财付通记录编号"),
            @ApiImplicitParam(name="jyjeRange",value = "交易金额范围"),
            @ApiImplicitParam(name="dsId",value = "对手账号"),
            @ApiImplicitParam(name="zcType",value = "整除类型")})
    public Object analyzeJyls(HttpServletRequest request,String cftId,String jyjeRange,String dsId,String zcType) {
        try {
            if(StringUtils.isBlank(cftId) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"财付通记录编号为空"));
            }
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxCftService.analyzeJyls(cftId,jyjeRange,dsId,zcType,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("统计成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("统计失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
    /**
     * 分析可疑人信息接口
     *
     * @return
     */
    @ApiOperation(value = "财付通对手统计", response = Result.class)
    @RequestMapping(value = "/jyds",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="cftId",value = "财付通记录编号"),
            @ApiImplicitParam(name="zcType",value = "整除类型")})
    public Object analyzeJyds(HttpServletRequest request,String cftId,String zcType) {
        try {
            if(StringUtils.isBlank(cftId) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"财付通记录编号为空"));
            }

            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxCftService.analyzeJyds(cftId,zcType,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("统计成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("统计失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

    /**
     * 财付通流水金额分组
     *
     * @return
     */
    @ApiOperation(value = "财付通流水金额分组", response = Result.class)
    @RequestMapping(value = "/jyje",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="cftId",value = "财付通记录编号"),
            @ApiImplicitParam(name="dsId",value = "对手账号"),
            @ApiImplicitParam(name="zcType",value = "整除类型")})
    public Object analyzeJyje(HttpServletRequest request,String cftId,String dsId,String zcType) {
        try {
            if(StringUtils.isBlank(cftId) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"财付通记录编号为空"));
            }

            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxCftService.analyzeJyje(cftId,dsId,zcType,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("统计成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("统计失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

    /**
     * 财付通流水被100整除的数据量
     *
     * @return
     */
    @ApiOperation(value = "财付通流水被100整除的数据量", response = Result.class)
    @RequestMapping(value = "/jyjeZc100",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="cftId",value = "财付通记录编号"),
            @ApiImplicitParam(name="dsId",value = "对手账号")})
    public Object analyzeJyjeZc100(HttpServletRequest request,String cftId,String dsId) {
        try {
            if(StringUtils.isBlank(cftId) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"财付通记录编号为空"));
            }

            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxCftService.analyzeZc100(cftId,dsId,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("统计成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("统计失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
    /**
     * 导出excel
     *
     * @return
     */
    @ApiOperation(value = "exportExcel", response = Result.class)
    @RequestMapping(value = "/exportExcel",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="cftId",value = "财付通记录编号"),
            @ApiImplicitParam(name="dsId",value = "对手账号")})
    public Object exportExcel(HttpServletRequest request, HttpServletResponse response,
                              String title, String hzid, String qjfbid, String jefbid, String dshzid) {
        try {
            if(StringUtils.isBlank(title) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"导出标题为空"));
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title",title);
            jsonObject.put("hzid",hzid);
            jsonObject.put("qjfbid",qjfbid);
            jsonObject.put("jefbid",jefbid);
            jsonObject.put("dshzid",dshzid);
            tjfxCftService.exportExcel(request,response,jsonObject,title);
            return null;
        } catch (Exception exception) {
            LOGGER.error("导出失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
}