package com.anluy.admin.eqa.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/6/13.
 */
public class MD5 {
    /**
     * 生成32位md5码
     *
     * @param key
     * @return
     */
    public static String encode(String key) {

        try {
            // 得到一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(key.getBytes());
            StringBuffer buffer = new StringBuffer();
            // 把每一个byte 做一个与运算 0xff;
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }

            // 标准的md5加密后的结果
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
