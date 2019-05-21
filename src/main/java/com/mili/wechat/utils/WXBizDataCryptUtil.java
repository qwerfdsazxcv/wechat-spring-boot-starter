package com.mili.wechat.utils;

import org.bouncycastle.util.encoders.Base64;

import java.io.UnsupportedEncodingException;

/**
 * 〈微信小程序密文数据解密〉
 *
 * @author yumingli
 * @create 2019/3/29
 * @since 1.0.0
 */
public class WXBizDataCryptUtil {
  private String appId;
  private String sessionKey;
  public WXBizDataCryptUtil(String appId, String sessionKey) {
    this.appId = appId;
    this.sessionKey = sessionKey;
  }

  public String decrypt(String encryptedData,String iv) throws UnsupportedEncodingException {
    byte[] content= Base64.decode(encryptedData);
    byte[] aesKey= Base64.decode(sessionKey);
    byte[] ivByte= Base64.decode(iv);
    byte[] data= AESUtils.decrypt(content,aesKey,ivByte);
    String decoded=new String(data,"UTF-8");
    return decoded;
  }
}
