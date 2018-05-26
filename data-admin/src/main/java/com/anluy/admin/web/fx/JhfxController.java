package com.anluy.admin.web.fx;

import com.anluy.admin.entity.Suspicious;
import com.anluy.admin.service.JhFxService;
import com.anluy.admin.service.SuspiciousService;
import com.anluy.admin.web.AuthorizationController;
import com.anluy.commons.web.Result;
import io.swagger.annotations.Api;
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
import java.util.Date;

/**
 * 功能说明：集合分析
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/admin/fx")
@Api(value = "/api/admin/fx", description = "集合分析")
public class JhfxController {
    private static final Logger LOGGER = LoggerFactory.getLogger(JhfxController.class);

    @Resource
    private JhFxService jhFxService;

    /**
     * 分析可疑人信息接口
     *
     * @return
     */
    @ApiOperation(value = "集合分析", response = Result.class)
    @RequestMapping(value = "/jhfx",method = {RequestMethod.GET,RequestMethod.POST})
    public Object analyze(HttpServletRequest request,String paramsArr, String fieldArr) {
        try {
            if(StringUtils.isBlank(paramsArr) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"查询语句为空"));
            }
            if(StringUtils.isBlank(fieldArr) ){
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001,"比对字段为空"));
            }
            String token = request.getHeader(AuthorizationController.AUTHORIZATION);
            Object result = jhFxService.analyze(paramsArr,fieldArr,token);
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("比对成功").setData(result).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("比对失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage()));
        }
    }

}