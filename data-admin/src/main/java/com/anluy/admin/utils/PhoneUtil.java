package com.anluy.admin.utils;

import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2019/1/12.
 */
public class PhoneUtil {
    private static PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    private static PhoneNumberToCarrierMapper carrierMapper = PhoneNumberToCarrierMapper.getInstance();
    private static PhoneNumberOfflineGeocoder geocoder = PhoneNumberOfflineGeocoder.getInstance();

    /**
     * 根据国家代码和手机号  判断手机号是否有效
     *
     * @param phoneNumber
     * @param countryCode
     * @return
     */
    public static boolean checkPhoneNumber(String phoneNumber, String countryCode) {

        int ccode = Integer.valueOf(countryCode);
        long phone = Long.valueOf(phoneNumber);

        Phonenumber.PhoneNumber pn = new Phonenumber.PhoneNumber();
        pn.setCountryCode(ccode);
        pn.setNationalNumber(phone);

        return phoneNumberUtil.isValidNumber(pn);

    }

    /**
     * 根据国家代码和手机号  判断手机运营商
     *
     * @param phoneNumber
     * @param countryCode
     * @return
     */
    public static String getCarrier(String phoneNumber, String countryCode) {
        int ccode = Integer.valueOf(countryCode);
        long phone = Long.valueOf(phoneNumber);

        Phonenumber.PhoneNumber pn = new Phonenumber.PhoneNumber();
        pn.setCountryCode(ccode);
        pn.setNationalNumber(phone);
        //返回结果只有英文，自己转成成中文
        String carrierEn = carrierMapper.getNameForNumber(pn, Locale.ENGLISH);
        String carrierZh = "";
        //carrierZh += geocoder.getDescriptionForNumber(pn, Locale.CHINESE);
        switch (carrierEn) {
            case "China Mobile":
                carrierZh += "中国移动";
                break;
            case "China Unicom":
                carrierZh += "中国联通";
                break;
            case "China Telecom":
                carrierZh += "中国电信";
                break;
            default:
                break;
        }
        return carrierZh;
    }


    /**
     * @param @param  phoneNumber
     * @param @param  countryCode
     * @param @return 参数
     * @throws
     * @Description: 根据国家代码和手机号  手机归属地
     * @date 2015-7-13 上午11:33:18
     */
    public static String getGeo(String phoneNumber, String countryCode) {

        int ccode = Integer.valueOf(countryCode);
        long phone = Long.valueOf(phoneNumber);

        Phonenumber.PhoneNumber pn = new Phonenumber.PhoneNumber();
        pn.setCountryCode(ccode);
        pn.setNationalNumber(phone);
        return geocoder.getDescriptionForNumber(pn, Locale.CHINA);
    }

    /**
     * 根据手机号码获取归属地和运营商
     *
     * @param phoneNumber
     * @return
     */
    public static Map getPhoneInfo(String phoneNumber) {
        return getPhoneInfo(phoneNumber, "86");
    }

    /**
     * 根据手机号码获取归属地和运营商
     *
     * @param phoneNumber
     * @param countryCode
     * @return
     */
    public static Map getPhoneInfo(String phoneNumber, String countryCode) {
        if (!checkPhoneNumber(phoneNumber, countryCode)) {
            return null;
        }
        String carrier = getCarrier(phoneNumber, countryCode);
        String city = getGeo(phoneNumber, countryCode);
        Map map = new HashMap<>();
        map.put("city", city);
        map.put("carrier", carrier);
        map.put("phone", phoneNumber);
        return map;
    }

    public static void main(String[] args) {
        System.out.println(PhoneUtil.getPhoneInfo("13430473453"));
    }
}

