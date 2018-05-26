package com.anluy.admin.web.fx;

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
     * 分析可疑人信息接口
     *
     * @return
     */
    @ApiOperation(value = "财付通流水统计", response = Result.class)
    @RequestMapping(value = "/jyls",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="cftId",value = "财付通记录编号")})
    public Object analyzeJyls(HttpServletRequest request,String cftId) {
        try {
            if(StringUtils.isBlank(cftId) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"财付通记录编号为空"));
            }
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxCftService.analyzeJyls(cftId,token);
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
            @ApiImplicitParam(name="cftzh",value = "财付通账号")})
    public Object analyzeJyds(HttpServletRequest request,String cftId,String cftzh) {
        try {
            if(StringUtils.isBlank(cftId) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"财付通记录编号为空"));
            }
            if(StringUtils.isBlank(cftzh) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"财付通账号为空"));
            }
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxCftService.analyzeJyds(cftId,cftzh,token);
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
    @ApiImplicitParams({@ApiImplicitParam(name="cftId",value = "财付通记录编号")})
    public Object analyzeJyje(HttpServletRequest request,String cftId) {
        try {
            if(StringUtils.isBlank(cftId) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"财付通记录编号为空"));
            }
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxCftService.analyzeJyje(cftId,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("统计成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("统计失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
}