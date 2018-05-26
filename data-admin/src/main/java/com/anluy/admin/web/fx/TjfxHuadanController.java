package com.anluy.admin.web.fx;

import com.anluy.admin.service.TjfxCftService;
import com.anluy.admin.service.TjfxHuadanService;
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
 * 功能说明：话单流水分析
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/fx/huadan")
@Api(value = "/api/admin/fx/huadan", description = "话单流水分析")
public class TjfxHuadanController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TjfxHuadanController.class);

    @Resource
    private TjfxHuadanService tjfxHuadanService;

    /**
     * 分析可疑人信息接口
     *
     * @return
     */
    @ApiOperation(value = "话单流水统计", response = Result.class)
    @RequestMapping(value = "/hdls",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="hdId",value = "话单记录编号")})
    public Object analyzeJyls(HttpServletRequest request,String hdId) {
        try {
            if(StringUtils.isBlank(hdId) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"话单记录编号为空"));
            }
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxHuadanService.analyzeThjl(hdId,token);
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
    @ApiOperation(value = "话单对手统计", response = Result.class)
    @RequestMapping(value = "/hdds",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="hdId",value = "话单记录编号")})
    public Object analyzeJyds(HttpServletRequest request,String hdId) {
        try {
            if(StringUtils.isBlank(hdId) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"话单记录编号为空"));
            }
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxHuadanService.analyzeDdxx(hdId,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("统计成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("统计失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
    /**
     * 话单流水时长分组
     *
     * @return
     */
    @ApiOperation(value = "话单流水时长分组", response = Result.class)
    @RequestMapping(value = "/thsc",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name="hdId",value = "话单记录编号")})
    public Object analyzeThsc(HttpServletRequest request,String hdId) {
        try {
            if(StringUtils.isBlank(hdId) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"话单记录编号为空"));
            }
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = tjfxHuadanService.analyzeThsc(hdId,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("统计成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("统计失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }
}