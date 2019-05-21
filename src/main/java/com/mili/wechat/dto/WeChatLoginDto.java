package com.mili.wechat.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 〈微信登录请求〉
 *
 * @author yumingli
 * @create 2019/4/26
 * @since 1.0.0
 */
@Data
public class WeChatLoginDto implements Serializable {
  private static final long serialVersionUID = -8559710668627317019L;
  /**
   * 微信code
   */
  private String code;
  /**
   * 签名报文
   */
  private String rawData;
  /**
   * 微信返回报文签名
   */
  private String signature;
  /**
   * 加密用户信息
   */
  private String encryptedData;
  /**
   * 加密向量
   */
  private String iv;
}
