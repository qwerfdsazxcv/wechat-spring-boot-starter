package com.mili.wechat.filter;

import com.alibaba.fastjson.JSON;
import com.mili.wechat.config.WeChatProperties;
import com.mili.wechat.dto.UserContext;
import com.mili.wechat.dto.WeChatUserInfoDto;
import com.mili.wechat.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class LoginFilter extends OncePerRequestFilter {
  @Resource
  private RedisTemplate redisTemplate;
  @Resource
  private WeChatProperties properties;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if (null == request.getHeader("userToken")) {
      log.info("前端未上传userToken");
      ResponseUtil.writeAjaxLoginFail(response, properties.getUnloginCode(), "userToken in header is null");
      return;
    }

    Object redisUserToken = redisTemplate.opsForValue().get(request.getHeader("userToken"));
    if (null != redisUserToken) {
      WeChatUserInfoDto userInfoDto = JSON.parseObject(redisUserToken.toString(), WeChatUserInfoDto.class);
      UserContext.setCurrentUser(userInfoDto);
    } else {
      log.info("登录信息已失效");
      ResponseUtil.writeAjaxLoginFail(response, properties.getUnloginCode(), "userToken in header is invalid");
      return;
    }
    filterChain.doFilter(request, response);
    UserContext.remove();
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return properties.getNotFilterUrls().contains(request.getRequestURI());

  }
}
