package com.mili.wechat.config;

import com.mili.wechat.client.WeChatTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 〈微信工具自动配置类〉
 *
 * @author yumingli
 * @create 2019/5/15
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(WeChatTemplate.class)
@EnableConfigurationProperties(com.mili.wechat.config.WeChatProperties.class)
public class WeChatAutoConfigure {
  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(prefix = "weixin.service",value="enabled",havingValue = "true")
  WeChatTemplate weChatTemplate(){
    return new WeChatTemplate();
  }
}
