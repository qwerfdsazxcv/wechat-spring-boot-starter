package com.mili.wechat.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 微信用户信息
 */
@Data
@Accessors(chain = true)
public class WeChatUserInfoDto implements Serializable {

    private static final long serialVersionUID = -5648052814306432955L;
    /**
     * 用户id
     */
    String id;
    private String openId;
    private String nickName;
    private Integer gender;
    private String language;
    private String city;
    private String province;
    private String country;
    private String avatarUrl;
    private String unionId;
    private String sessionKey;

}
