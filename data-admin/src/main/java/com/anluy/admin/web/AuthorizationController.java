package com.anluy.admin.web;

import com.anluy.admin.interceptor.AuthorizedInterceptor;
import com.anluy.commons.web.Result;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.swagger.annotations.*;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 功能说明：用户认证中心
 * <p>
 * Created by hc.zeng on 2017/11/5.
 */
@RestController
@RequestMapping("/api/admin/authorization")
@Api(value = "/api/admin/authorization", description = "用户认证的接口")
public class AuthorizationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationController.class);
    private static final String SECRET = "www.anluy.com";
    private static final String ISSUER = "www.anluy.com";
    private static final String LOGININFO = "loginInfo";
    private static final String AUTHORIZATION = "Authorization";
    private final static String CACHE_NAME = "Authorization-Cache";

    @Resource
    private Environment environment;

    @Resource
    private CacheManager cacheManager;
    /**
     * 授权接口
     *
     * @param userName
     * @param password
     * @return
     */
    @ApiOperation(value = "授权接口", response = Result.class)
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query")
    })
    @ApiResponses(value = {@ApiResponse(code = 401, message = "认证失败")})//错误码说明
    @RequestMapping(value = "/authorize", method = RequestMethod.POST)
    public Object authorize(@RequestParam(name = "userName") String userName,
                            @RequestParam(name = "password") String password) {
        try {
            String _userName = environment.getProperty("admin.userName","admin");
            String _password = environment.getProperty("admin.password","admin123");
            if(_userName.equals(userName)&_password.equals(password)){

                //获取授权算法，并生成token
                Algorithm algorithm = Algorithm.HMAC256(SECRET);
                String token = JWT.create()
                        .withIssuer(ISSUER).withClaim(LOGININFO, _userName)
                        .sign(algorithm);
                EhCacheCache cache = (EhCacheCache) cacheManager.getCache(CACHE_NAME);
                cache.put(token,token);
                return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("认证成功").setData(token));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(401,"认证失败"));
        } catch (UnsupportedEncodingException exception) {
            LOGGER.error("认证失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.UNAUTHORIZED.value(),"认证失败").setData(exception.getMessage()));
        } catch (JWTCreationException exception) {
            LOGGER.error("认证失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.UNAUTHORIZED.value(),"认证失败").setData(exception.getMessage()));
        }
    }

    /**
     * 授权令牌认证接口
     *
     * @param authorization
     * @return
     */
    @ApiOperation(value = "授权令牌认证接口", response = Result.class)
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "authorization", value = "令牌信息", required = true, dataType = "String", paramType = "query")
    })
    @ApiResponses(value = {@ApiResponse(code = 401, message = "验证失败")})//错误码说明
    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    public Object validate(HttpServletRequest request, @RequestParam(required = false) String authorization) {
        try {
            String token = request.getHeader(AUTHORIZATION);
            if (StringUtils.isBlank(token)) {
                token = authorization;
            }
            if (StringUtils.isBlank(token)) {
                LOGGER.error("验证失败，没有认证令牌信息");
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.UNAUTHORIZED.value(),"验证失败，没有认证令牌信息"));
            }
            //获取授权算法，并解析token
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            Map<String, Claim> claims = jwt.getClaims();
            Claim claim = claims.get(LOGININFO);//在token的负载信息中获取登陆信息

            EhCacheCache cache = (EhCacheCache) cacheManager.getCache(CACHE_NAME);
            Object authCache = cache.get(token);
            if (authCache != null) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("验证成功").setData(claim.asString()));
            }
            LOGGER.error("令牌已过期");
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.UNAUTHORIZED.value(),"令牌已过期"));
        } catch (UnsupportedEncodingException exception) {
            LOGGER.error("验证失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.UNAUTHORIZED.value(),"认证失败，无效的令牌"));
        } catch (JWTVerificationException exception) {
            LOGGER.error("验证失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.UNAUTHORIZED.value(),"认证失败，无效的令牌"));
        }
    }

    /**
     * 授权令牌注销证口
     *
     * @param request
     * @param authorization
     * @return
     */
    @ApiOperation(value = "授权令牌注销证口", response = Result.class)
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "authorization", value = "令牌信息", required = true, dataType = "String", paramType = "query")
    })
    @ApiResponses(value = {@ApiResponse(code = 401, message = "验证失败")})//错误码说明
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Object logout(HttpServletRequest request, @RequestParam(required = false) String authorization) {
        try {
            String token = request.getHeader(AUTHORIZATION);
            if (StringUtils.isBlank(token)) {
                token = authorization;
            }
            if (StringUtils.isBlank(token)) {
                LOGGER.error("注销失败，没有认证令牌信息");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("注销失败，没有认证令牌信息");
            }
            //获取授权算法，并解析token
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            Map<String, Claim> claims = jwt.getClaims();    //Key is the Claim name
            Claim claim = claims.get(LOGININFO);
            EhCacheCache cache = (EhCacheCache) cacheManager.getCache(CACHE_NAME);
            Object authCache = cache.get(token);
            if (authCache != null) {
                cache.evict(token);
                return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("注销成功").setData(claim.asString()));
            }
            LOGGER.error("注销成功");
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("注销成功"));
        } catch (UnsupportedEncodingException exception) {
            LOGGER.error("注销失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),"注销失败，无效的令牌"));
        } catch (JWTVerificationException exception) {
            LOGGER.error("注销失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),"注销失败，无效的令牌"));
        }
    }
}
