package com.anluy.admin.web.fx;

import com.anluy.admin.service.TjfxCftService;
import com.anluy.admin.service.TjfxYhzhService;
import com.anluy.admin.web.AuthorizationController;
import com.anluy.commons.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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

/**
 * 功能说明：银行流水流水分析
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/fx/yhzh")
@Api(value = "/api/admin/fx/yhzh", description = "银行流水流水分析")
public class TjfxYhzhController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TjfxYhzhController.class);

    @Resource
    private TjfxYhzhService tjfxYhzhService;

    /**
     * 分析可疑人信息接口
     *
     * @return
     */
    @ApiOperation(value = "银行流水流水统计", response = Result.class)
    @RequestMapping(value = "/jyls",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="ssyh",value = "所属银行"),
            @ApiImplicitParam(name="kh",value = "银行卡号"),
            @ApiImplicitParam(name="zh",value = "银行账号"),
            @ApiImplicitParam(name="jyjeRange",value = "交易金额范围"),
            @ApiImplicitParam(name="dsId",value = "对手账号"),
            @ApiImplicitParam(name="zcType",value = "整除类型")})
    public Object analyzeJyls(HttpServletRequest request,String ssyh,String kh,String zh,String jyjeRange,String dsId,String zcType) {
        try {
            if(StringUtils.isBlank(ssyh) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"所属银行为空"));
            }
            if(StringUtils.isBlank(kh) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"银行卡号为空"));
            }
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxYhzhService.analyzeJyls(ssyh,kh,zh,jyjeRange,dsId,zcType,token);
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
    @ApiOperation(value = "银行流水对手统计", response = Result.class)
    @RequestMapping(value = "/jyds",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="ssyh",value = "所属银行"),
            @ApiImplicitParam(name="kh",value = "银行卡号"),
            @ApiImplicitParam(name="zh",value = "银行账号"),
            @ApiImplicitParam(name="zcType",value = "整除类型")})
    public Object analyzeJyds(HttpServletRequest request,String ssyh,String kh,String zh,String zcType) {
        try {
            if(StringUtils.isBlank(ssyh) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"所属银行为空"));
            }
            if(StringUtils.isBlank(kh) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"银行卡号为空"));
            }

            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxYhzhService.analyzeJyds(ssyh,kh,zh,zcType,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("统计成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("统计失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

    /**
     * 银行流水流水金额分组
     *
     * @return
     */
    @ApiOperation(value = "银行流水流水金额分组", response = Result.class)
    @RequestMapping(value = "/jyje",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="ssyh",value = "所属银行"),
            @ApiImplicitParam(name="kh",value = "银行卡号"),
            @ApiImplicitParam(name="zh",value = "银行账号"),
            @ApiImplicitParam(name="dsId",value = "对手账号"),
            @ApiImplicitParam(name="zcType",value = "整除类型")})
    public Object analyzeJyje(HttpServletRequest request,String ssyh,String kh,String zh,String dsId,String zcType) {
        try {
            if(StringUtils.isBlank(ssyh) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"所属银行为空"));
            }
            if(StringUtils.isBlank(kh) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"银行卡号为空"));
            }

            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxYhzhService.analyzeJyje(ssyh,kh,zh,dsId,zcType,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("统计成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("统计失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

    /**
     * 银行流水流水被100整除的数据量
     *
     * @return
     */
    @ApiOperation(value = "银行流水流水被100整除的数据量", response = Result.class)
    @RequestMapping(value = "/jyjeZc100",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="ssyh",value = "所属银行"),
            @ApiImplicitParam(name="kh",value = "银行卡号"),
            @ApiImplicitParam(name="zh",value = "银行账号"),
            @ApiImplicitParam(name="dsId",value = "对手账号")})
    public Object analyzeJyjeZc100(HttpServletRequest request,String ssyh,String kh,String zh,String dsId) {
        try {
            if(StringUtils.isBlank(ssyh) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"所属银行为空"));
            }
            if(StringUtils.isBlank(kh) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"银行卡号为空"));
            }

            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxYhzhService.analyzeZc100(ssyh,kh,zh,dsId,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("统计成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("统计失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
}