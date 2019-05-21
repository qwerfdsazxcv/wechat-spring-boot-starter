package com.mili.wechat.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 微信令牌
 */
@Data
public class WeChatTokenDto {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Integer expiresIn;
}
