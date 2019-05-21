package com.mili.wechat.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;

@Slf4j
public class MD5Util {

    public static String encrypt(String plainText){
        //确定计算方法
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(plainText.getBytes());
            byte b[] = md5.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString().toUpperCase();
        }catch (Exception ex){
            log.info(ex.getMessage(),ex);
        }
        return null;
    }

    /**
     * 加密
     *
     * @param source 密码（明文）
     * @return 密码（密文）
     */
    public static String encryptByMd5(String source) {
        try {
            return DigestUtils.md5Hex(source);
        } catch (Exception e) {
            log.warn("加密异常: error={}", e);
            return source;
        }
    }

}
