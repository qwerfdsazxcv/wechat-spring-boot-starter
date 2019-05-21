package com.mili.wechat.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 〈Spring http  client  配置〉
 *
 * @author yumingli
 * @create 2019/5/20
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(RestTemplate.class)
public class RestTemplateAutoConfigure {

  @Bean
  @ConditionalOnMissingBean
  public RestTemplate restTemplate(){
    return new RestTemplate();
  }
}
