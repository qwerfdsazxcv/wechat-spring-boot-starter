package com.mili.wechat.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class WeChatCode2SessionDto implements Serializable {

    private static final long serialVersionUID = 2413278017503651674L;
    private String errcode;
    private String errmsg;
    private String openid;
    @JSONField(name="session_key")
    private String sessionKey;
}
