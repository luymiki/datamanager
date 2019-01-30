package com.anluy.admin.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.utils.BankBinUtil;
import com.anluy.admin.utils.IPAddrUtil;
import com.anluy.admin.utils.PhoneAddrUtil;
import com.anluy.commons.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.ipip.ipdb.CityInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能说明：ip、电话归属地查询
 * <p>
 * Created by hc.zeng on 2019/1/12.
 */
@RestController
@RequestMapping("/api/admin/location")
@Api(value = "/api/admin/location", description = "ip、电话归属地查询")
public class LocationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationController.class);

    @Resource
    private IPAddrUtil ipAddrUtil;
    @Resource
    private PhoneAddrUtil phoneAddrUtil;
    @Resource
    private BankBinUtil bankBinUtil;

    /**
     * 查询IP归属地信息接口
     *
     * @return
     */
    @ApiOperation(value = "查询IP归属地信息接口", response = Result.class)
    @RequestMapping(value = "/ip", method = {RequestMethod.GET, RequestMethod.POST})
    public Object ip(HttpServletRequest request, @RequestBody String[] ip) {
        try {
            if (ip == null || ip.length == 0) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "待查询的IP为空"));
            }
            List<JSONObject> list = new ArrayList<>();
            for (String i : ip) {
                if(StringUtils.isBlank(i)){
                    continue;
                }
                CityInfo info = ipAddrUtil.findCityInfo(i);
                if(info!=null){
                    JSONObject object = (JSONObject) JSON.toJSON(info);
                    object.put("ip",i);
                    list.add(object);
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("查询成功").setData(list).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("查询失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
        }
    }

    /**
     * 查询电话号码归属地信息接口
     *
     * @return
     */
    @ApiOperation(value = "查询电话号码归属地信息接口", response = Result.class)
    @RequestMapping(value = "/phone", method = {RequestMethod.GET, RequestMethod.POST})
    public Object phone(HttpServletRequest request,  @RequestBody String[] phone) {
        try {
            if (phone == null || phone.length == 0) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "待查询的电话号码为空"));
            }
            List<Map> list = new ArrayList<>();
            for (String i : phone) {
                if(StringUtils.isBlank(i)){
                    continue;
                }
                Map map = phoneAddrUtil.getPhoneInfo(i);
                if(map!=null){
                    list.add(map);
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("查询成功").setData(list).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("查询失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
        }
    }
    /**
     * 查询银行卡卡BIN信息接口
     *
     * @return
     */
    @ApiOperation(value = "查询银行卡卡BIN信息接口", response = Result.class)
    @RequestMapping(value = "/bankbin", method = {RequestMethod.GET, RequestMethod.POST})
    public Object bankbin(HttpServletRequest request,  @RequestBody String[] yhkh) {
        try {
            if (yhkh == null || yhkh.length == 0) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "待查询的银行卡号码为空"));
            }
            Map<String,List<Map>> list = new LinkedHashMap();
            for (String i : yhkh) {
                if(StringUtils.isBlank(i)){
                    continue;
                }
                List<Map> map = bankBinUtil.getBankBinInfo(i);
                if(map!=null){
                    list.put(i,map);
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("查询成功").setData(list).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("查询失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
        }
    }


}
