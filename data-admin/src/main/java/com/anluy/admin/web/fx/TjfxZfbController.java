package com.anluy.admin.web.fx;

import com.anluy.admin.service.TjfxZfbService;
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
 * 功能说明：支付宝流水分析
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/fx/zfb")
@Api(value = "/api/admin/fx/zfb", description = "支付宝流水分析")
public class TjfxZfbController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TjfxZfbController.class);

    @Resource
    private TjfxZfbService tjfxZfbService;

    /**
     * 支付宝流水整合接口
     *
     * @return
     */
    @ApiOperation(value = "支付宝流水整合", response = Result.class)
    @RequestMapping(value = "/integrated",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="userId",value = "支付宝账号"),
            @ApiImplicitParam(name="xcbh",value = "协查编号")})
    public Object integrated(HttpServletRequest request,String userId,String xcbh) {
        try {
            if(StringUtils.isBlank(userId) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"支付宝账号为空"));
            }
            if(StringUtils.isBlank(xcbh) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"协查编号为空"));
            }
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxZfbService.integrated(userId,xcbh,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("整合成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("整合失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
    /**
     * 支付宝流水统计接口
     *
     * @return
     */
    @ApiOperation(value = "支付宝流水统计", response = Result.class)
    @RequestMapping(value = "/jyls",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="userId",value = "支付宝账号"),
            @ApiImplicitParam(name="xcbh",value = "协查编号")})
    public Object analyzeJyls(HttpServletRequest request,String userId,String xcbh) {
        try {
            if(StringUtils.isBlank(userId) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"支付宝账号为空"));
            }
            if(StringUtils.isBlank(xcbh) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"协查编号为空"));
            }
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxZfbService.analyzeJyls(userId,xcbh,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("统计成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("统计失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

    /**
     * 支付宝对手统计接口
     *
     * @return
     */
    @ApiOperation(value = "支付宝对手统计", response = Result.class)
    @RequestMapping(value = "/jyds",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="userId",value = "支付宝账号"),
            @ApiImplicitParam(name="xcbh",value = "协查编号")})
    public Object analyzeJyds(HttpServletRequest request,String userId,String xcbh) {
        try {
            if(StringUtils.isBlank(userId) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"支付宝账号为空"));
            }
            if(StringUtils.isBlank(xcbh) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"协查编号为空"));
            }
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxZfbService.analyzeJyds(userId,xcbh,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("统计成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("统计失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

    /**
     * 支付宝流水金额分组
     *
     * @return
     */
    @ApiOperation(value = "支付宝流水金额分组", response = Result.class)
    @RequestMapping(value = "/jyje",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="userId",value = "支付宝账号"),
            @ApiImplicitParam(name="xcbh",value = "协查编号")})
    public Object analyzeJyje(HttpServletRequest request,String userId,String xcbh) {
        try {
            if(StringUtils.isBlank(userId) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"支付宝账号为空"));
            }
            if(StringUtils.isBlank(xcbh) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"协查编号为空"));
            }
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxZfbService.analyzeJyje(userId,xcbh,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("统计成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("统计失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
}