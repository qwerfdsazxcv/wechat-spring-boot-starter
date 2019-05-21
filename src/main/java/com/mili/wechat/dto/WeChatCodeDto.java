package com.mili.wechat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 〈获取小程序二维码请求〉
 *
 * @author yumingli
 * @create 2019/4/25
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class WeChatCodeDto {
  /**
   * 二维码缓存key
   */
  private String miniCodeKey;
  private String scene;
  @JsonProperty("is_hyaline")
  private boolean hyaline;
  private String page;
  @JsonProperty("line_color")
  private Object lineColor;

}
