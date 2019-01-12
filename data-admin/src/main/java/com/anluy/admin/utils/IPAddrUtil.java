package com.anluy.admin.utils;

import net.ipip.ipdb.City;
import net.ipip.ipdb.CityInfo;
import net.ipip.ipdb.IPFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * 功能说明：ip地址查询
 * <p>
 * Created by hc.zeng on 2019/1/12.
 */
public class IPAddrUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(IPAddrUtil.class);
    private InputStream inputStream;
    // City类可用于IPDB格式的IPv4免费库，IPv4与IPv6的每周高级版、每日标准版、每日高级版、每日专业版、每日旗舰版
    private City db;

    public IPAddrUtil(String ipdbPath) {
        try {
            this.inputStream = new FileInputStream(ipdbPath);
            db = new City(inputStream);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public IPAddrUtil(InputStream inputStream) {
        try {
            this.inputStream = inputStream;
            db = new City(inputStream);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 根据IP查询归属地信息
     * @param ip
     * @return
     */
    public String findCityInfoString(String ip) {
        CityInfo cityInfo = this.findCityInfo(ip);
        if(cityInfo!=null){
            return cityInfo.getCountryName() + cityInfo.getRegionName() + cityInfo.getCityName();
        }
        return null;
    }
    /**
     * 根据IP查询归属地信息
     * @param ip
     * @return
     */
    public CityInfo findCityInfo(String ip) {
        return this.findCityInfo(ip,"CN");
    }

    /**
     * 根据IP查询归属地信息
     * @param ip
     * @param language
     * @return
     */
    public CityInfo findCityInfo(String ip, String language) {
        try {
            CityInfo info = db.findInfo(ip, language);
            return info;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public static void main(String[] args) throws IOException, IPFormatException {
        City db = new City("G:\\GitHubProject\\datamanager\\data-admin\\src\\main\\resources\\ipipfree.ipdb");
        System.out.println(db.buildTime());
        System.out.println(db.languages());
        System.out.println(db.fields());
        System.out.println(db.isIPv4());
        System.out.println(db.isIPv6());

        System.out.println(Arrays.toString(db.find("118.28.1.1", "CN")));
    }
}
