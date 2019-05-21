package com.mili.wechat.dto;

import org.springframework.core.NamedThreadLocal;

import java.util.HashMap;
import java.util.Map;

public class UserContext {

  public static final ThreadLocal<Map<String, Object>> USER_THREAD_LOCAL = new NamedThreadLocal<Map<String, Object>>("user") {
    @Override
    protected Map<String, Object> initialValue() {
      return new HashMap<String, Object>();
    }
  };

  private static String userKey = "wechat_userInfo";

  /**
   * 保存当前登录用户信息
   *
   * @param user
   */
  public static void setCurrentUser(WeChatUserInfoDto user) {
    Map contextMap = USER_THREAD_LOCAL.get();
    if (contextMap == null) {
      USER_THREAD_LOCAL.set(new HashMap<>());
    }
    USER_THREAD_LOCAL.get().put(userKey, user);
  }

  /**
   * 获取当前登录用户
   *
   * @return
   */
  public static WeChatUserInfoDto getCurrentUser() {
    return (WeChatUserInfoDto) USER_THREAD_LOCAL.get().get(userKey);
  }


  /**
   * 获取登录用户ID
   *
   * @return
   */
  public static String getUserId() {
    WeChatUserInfoDto user = getCurrentUser();
    if (user == null) {
      return null;
    }
    return user.getId();
  }

  public static void remove() {
    USER_THREAD_LOCAL.remove();
  }
}
