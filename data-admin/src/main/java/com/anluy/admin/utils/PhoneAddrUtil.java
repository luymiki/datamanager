package com.anluy.admin.utils;

import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2019/1/12.
 */
public class PhoneAddrUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneAddrUtil.class);
    private static final Map<String,Map> dataMap = new HashMap<>();
    @Resource
    private JdbcTemplate jdbcTemplate;

    public PhoneAddrUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        init();
    }

    private void init(){
        LOGGER.info("加载手机归属地数据。。。。");
        jdbcTemplate.query("select id,pref,phone,province,city,isp,post_code,city_code,area_code,types from phone ", new RowMapper<Map>() {
            @Override
            public Map mapRow(ResultSet resultSet, int i) throws SQLException {
                int count = resultSet.getMetaData().getColumnCount();
                Map<String,Object> map = new HashMap<>();
                for (int j = 1; j <= count; j++) {
                    map.put(resultSet.getMetaData().getColumnLabel(j).toLowerCase(),resultSet.getObject(j));
                }
                dataMap.put((String) map.get("phone"),map);
                return null;
            }
        });
        LOGGER.info("加载手机归属地数据完成。。。。");
    }
    /**
     * 根据手机号码获取归属地和运营商
     *
     * @param phoneNumber
     * @return
     */
    public Map getPhoneInfo(String phoneNumber) {
        if(StringUtils.isBlank(phoneNumber)){
            return null;
        }
        phoneNumber = phoneNumber.trim();
        if(phoneNumber.length()!= 11){
            return null;
        }
        String phone  = phoneNumber.substring(0,7);
        Map map = dataMap.get(phone);
        if(map==null){
            return null;
        }
        String carrier = (String) map.get("isp");
        String city = (String) map.get("province")+(String) map.get("city")+"市";
        Map result = new HashMap<>();
        result.put("city", city);
        result.put("carrier", "中国"+carrier);
        result.put("phone", phoneNumber);
        return result;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}

