package com.mili.wechat.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 〈微信用户信息〉
 *
 * @author yumingli
 * @create 2019/4/25
 * @since 1.0.0
 */
@Document(collection = "wechat_user_info")
@Data
@CompoundIndex(name = "openIdIndex", def = "{'open_id': 1}", unique = true)
public class WeChatUserInfoDo extends BaseDo {
  @Field(value = "open_id")
  private String openId;
  @Field(value="nick_name")
  private String nickName;
  private Integer gender;
  private String language;
  private String city;
  private String province;
  private String country;
  @Field(value = "avatar_url")
  private String avatarUrl;
  @Field(value="union_id")
  private String unionId;
  private String mobile;

}
