package com.mili.wechat.enums;

public class Constants {

  public static final String SYSTEM = "system";

  public static final String DELETE_N = "N";

  public static final String DELETE_Y = "Y";

  public static final String LOGIN_USER = "LOGIN_USER_";

  public static final String PATH_GET_WECHAT_TOKEN = "/cgi-bin/token?grant_type=%s&appid=%s&secret=%s";
  public static final String PATH_JSCODE_2_SESSION_ = "/sns/jscode2session?grant_type=%s&appid=%s&secret=%s&js_code=%s";
  public static final String PATH_GET_WXACODE_UNLIMIT = "/wxa/getwxacodeunlimit?access_token=%s";

}
