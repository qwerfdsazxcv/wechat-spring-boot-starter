package com.mili.wechat.Exception;

import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;

/**
 * 〈微信登录异常〉
 *
 * @author yumingli
 * @create 2019/5/17
 * @since 1.0.0
 */
public class UnloginException extends RuntimeException {
  private String message;

  public UnloginException() {
    super();
  }

  public UnloginException(String message) {
    this.message = message;
  }

  public UnloginException(Throwable cause) {
    super(cause);
  }

  public UnloginException(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setMessage(String errMsg, Object... args) {
    this.message = MessageFormat.format(StringUtils.stripToEmpty(errMsg), args);
  }
}
