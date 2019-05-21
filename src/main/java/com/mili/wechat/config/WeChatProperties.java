package com.mili.wechat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 〈微信配置〉
 *
 * @author yumingli
 * @create 2019/5/14
 * @since 1.0.0
 */

@Data
@ConfigurationProperties(prefix = "weixin")
public class WeChatProperties {
  private Api api;
  private String tokenKey;
  private Long userTokenExpire;
  private String unloginCode;
  private List<String> notFilterUrls;

  public static class Api {
    private String appId;
    private String appSecret;
    private String grantType;
    private String url;

    public Api() {
    }

    public String getAppId() {
      return appId;
    }

    public void setAppId(String appId) {
      this.appId = appId;
    }

    public String getAppSecret() {
      return appSecret;
    }

    public void setAppSecret(String appSecret) {
      this.appSecret = appSecret;
    }

    public String getGrantType() {
      return grantType;
    }

    public void setGrantType(String grantType) {
      this.grantType = grantType;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }
  }

}
