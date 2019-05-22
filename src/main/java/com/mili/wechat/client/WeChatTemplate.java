package com.mili.wechat.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mili.wechat.Exception.UnloginException;
import com.mili.wechat.config.WeChatProperties;
import com.mili.wechat.dto.*;
import com.mili.wechat.entity.WeChatUserInfoDo;
import com.mili.wechat.enums.Constants;
import com.mili.wechat.utils.MD5Util;
import com.mili.wechat.utils.WXBizDataCryptUtil;
import com.mongodb.DuplicateKeyException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class WeChatTemplate {
  private static final Logger log = LoggerFactory.getLogger(WeChatTemplate.class);
  @Resource
  private RedisTemplate redisTemplate;
  @Resource
  private MongoTemplate mongoTemplate;
  @Resource
  private RestTemplate restTemplate;
  @Autowired
  private WeChatProperties weChatProperties;


  public String getToken() {
    String token = (String) redisTemplate.opsForValue().get(weChatProperties.getTokenKey());
    if (StringUtils.isNotEmpty(token)) {
      return token;
    }
    return resetToken();
  }

  public String resetToken() {
    String token = null;
    String reqUrl = weChatProperties.getApi().getUrl() + String.format(Constants.PATH_GET_WECHAT_TOKEN, weChatProperties.getApi().getGrantType(), weChatProperties.getApi().getAppId(), weChatProperties.getApi().getAppSecret());
    ResponseEntity<WeChatTokenDto> result = restTemplate.getForEntity(reqUrl, WeChatTokenDto.class);
    WeChatTokenDto wechatTokenDto = result.getBody();
    token = wechatTokenDto.getAccessToken();
    Integer expireTime = wechatTokenDto.getExpiresIn();
    redisTemplate.opsForValue().set(weChatProperties.getTokenKey(), token, expireTime - 100L, TimeUnit.SECONDS);
    return token;
  }

  public WeChatCode2SessionDto getOpenSession(String code) {
    try {
      String reqUrl = weChatProperties.getApi().getUrl() + String.format(Constants.PATH_JSCODE_2_SESSION_, weChatProperties.getApi().getGrantType(), weChatProperties.getApi().getAppId(), weChatProperties.getApi().getAppSecret(), code);
      String result = restTemplate.getForObject(reqUrl, String.class);
      log.info("微信code 换取openSession 结果:{}", result);
      WeChatCode2SessionDto weiXinCode2Session = JSONObject.parseObject(result, WeChatCode2SessionDto.class);
      return weiXinCode2Session;
    } catch (Exception e) {
      log.error("根据Code 获取微信openSession异常:" + e);
      return null;
    }
  }


  public byte[] getWxacodeUnlimited(WeChatCodeDto weiXinCodeDto) {
    byte[] QRCode = null;
    Object obj = redisTemplate.opsForValue().get(weiXinCodeDto.getMiniCodeKey());
    if (null != obj) {
      QRCode = (byte[]) obj;
      return QRCode;
    }
    String token = getToken();
    String reqUrl = weChatProperties.getApi().getUrl() + String.format(Constants.PATH_GET_WXACODE_UNLIMIT, token);
    try {
      ResponseEntity<byte[]> responseEntity = restTemplate.postForEntity(reqUrl, weiXinCodeDto, byte[].class);
      if (MediaType.IMAGE_JPEG.equals(responseEntity.getHeaders().getContentType())) {
        QRCode = responseEntity.getBody();
        redisTemplate.opsForValue().set(weiXinCodeDto.getMiniCodeKey(), QRCode, -1);
        return QRCode;
      } else {
        log.info(new String(QRCode, "UTF-8"));
      }
    } catch (Exception e) {
      log.info("获取微信小程序码异常:" + e);
    }
    return null;
  }

  public String weChatLogin(WeChatLoginDto request) {
    WeChatCode2SessionDto code2SessionDto = this.getOpenSession(request.getCode());
    if (null == code2SessionDto) {
      throw new UnloginException("get wechat open session failed,open session is null");
    }
    //errorcode 不为空时，说明调动微信接口异常
    if (null != code2SessionDto.getErrcode()) {
      throw new UnloginException("get wechat open session failed," + code2SessionDto.getErrmsg());
    }
    Query query = new Query();
    query.addCriteria(Criteria.where("is_deleted").is(Constants.DELETE_N).and("open_id").is(code2SessionDto.getOpenid()));
    WeChatUserInfoDo weChatUserInfoDo = mongoTemplate.findOne(query, WeChatUserInfoDo.class);
    if (null != weChatUserInfoDo) {
      String userToken = Constants.LOGIN_USER.concat(MD5Util.encrypt(code2SessionDto.getOpenid() + System.currentTimeMillis() + RandomUtils.nextInt(9999)));
      WeChatUserInfoDto userInfoDto = new WeChatUserInfoDto();
      BeanUtils.copyProperties(weChatUserInfoDo, userInfoDto);
      //更新sessionKey
      userInfoDto.setSessionKey(code2SessionDto.getSessionKey());
      redisTemplate.opsForValue().set(userToken, JSON.toJSONString(userInfoDto), weChatProperties.getUserTokenExpire(), TimeUnit.SECONDS);
      return userToken;
    } else {
      try {
        weChatUserInfoDo = new WeChatUserInfoDo();
        //验签微信数据
        String verify = Hex.encodeHexString(DigestUtils.sha1(request.getRawData() + code2SessionDto.getSessionKey()));
        if (!request.getSignature().equalsIgnoreCase(verify)) {
          log.info("校验微信用户信息失败，获取的校验字符串:{},验证字符串:{}", request.getSignature(), verify);
          throw new UnloginException("check wechat signnature failed");
        }
        //解密用户信息
        WXBizDataCryptUtil dataCryptUtil = new WXBizDataCryptUtil(weChatProperties.getApi().getAppId(), code2SessionDto.getSessionKey());
        WeChatUserInfoDto userDto = new WeChatUserInfoDto();

        String data = dataCryptUtil.decrypt(request.getEncryptedData(), request.getIv());
        userDto = JSONObject.parseObject(data, WeChatUserInfoDto.class);
        if (null == userDto || null == userDto.getOpenId()) {
          log.info("解析微信用户信息失败，openid:{}", code2SessionDto.getOpenid());
          throw new UnloginException("decrypt wechat encryptedData failed");
        }
        log.info("微信用户信息:{}", userDto);

        BeanUtils.copyProperties(userDto, weChatUserInfoDo);

        weChatUserInfoDo.setOpenId(code2SessionDto.getOpenid());
        weChatUserInfoDo.setCreator(Constants.SYSTEM);
        weChatUserInfoDo.setModifier(Constants.SYSTEM);
        weChatUserInfoDo.setIsDeleted(Constants.DELETE_N);
        weChatUserInfoDo.setGmtCreated(new Date());
        weChatUserInfoDo.setGmtModified(new Date());

        mongoTemplate.save(weChatUserInfoDo);
      } catch (Exception e) {
        if (e instanceof DuplicateKeyException) {
          Query oldQuery = new Query();
          oldQuery.addCriteria(Criteria.where("open_id").is(code2SessionDto.getOpenid()));
          oldQuery.addCriteria(Criteria.where("is_deleted").is(Constants.DELETE_N));
          weChatUserInfoDo = mongoTemplate.findOne(oldQuery, WeChatUserInfoDo.class);
        }
      }
      String userToken = Constants.LOGIN_USER.concat(MD5Util.encrypt(code2SessionDto.getOpenid() + System.currentTimeMillis() + RandomUtils.nextInt(9999)));
      WeChatUserInfoDto userDto = new WeChatUserInfoDto();
      BeanUtils.copyProperties(weChatUserInfoDo, userDto);
      userDto.setSessionKey(code2SessionDto.getSessionKey());
      redisTemplate.opsForValue().set(userToken, JSON.toJSONString(userDto), weChatProperties.getUserTokenExpire(), TimeUnit.SECONDS);
      return userToken;
    }
  }

  public String weChatLogin(String code) {
    WeChatCode2SessionDto code2SessionDto = this.getOpenSession(code);
    if (null == code2SessionDto) {
      throw new UnloginException("get wechat open session failed,open session is null");
    }
    //errorcode 不为空时，说明调动微信接口异常
    if (null != code2SessionDto.getErrcode()) {
      throw new UnloginException("get wechat open session failed," + code2SessionDto.getErrmsg());
    }
    Query query = new Query();
    query.addCriteria(Criteria.where("is_deleted").is(Constants.DELETE_N).and("open_id").is(code2SessionDto.getOpenid()));
    WeChatUserInfoDo weChatUserInfoDo = mongoTemplate.findOne(query, WeChatUserInfoDo.class);
    if (null != weChatUserInfoDo) {
      String userToken = Constants.LOGIN_USER.concat(MD5Util.encrypt(code2SessionDto.getOpenid() + System.currentTimeMillis() + RandomUtils.nextInt(9999)));
      WeChatUserInfoDto userInfoDto = new WeChatUserInfoDto();
      BeanUtils.copyProperties(weChatUserInfoDo, userInfoDto);
      //更新sessionKey
      userInfoDto.setSessionKey(code2SessionDto.getSessionKey());
      redisTemplate.opsForValue().set(userToken, JSON.toJSONString(userInfoDto), weChatProperties.getUserTokenExpire(), TimeUnit.SECONDS);
      return userToken;
    } else {
      try {
        weChatUserInfoDo = new WeChatUserInfoDo();
        weChatUserInfoDo.setOpenId(code2SessionDto.getOpenid());
        weChatUserInfoDo.setCreator(Constants.SYSTEM);
        weChatUserInfoDo.setModifier(Constants.SYSTEM);
        weChatUserInfoDo.setIsDeleted(Constants.DELETE_N);
        weChatUserInfoDo.setGmtCreated(new Date());
        weChatUserInfoDo.setGmtModified(new Date());
        mongoTemplate.save(weChatUserInfoDo);
      } catch (Exception e) {
        if (e instanceof DuplicateKeyException) {
          Query oldQuery = new Query();
          oldQuery.addCriteria(Criteria.where("open_id").is(code2SessionDto.getOpenid()));
          oldQuery.addCriteria(Criteria.where("is_deleted").is(Constants.DELETE_N));
          weChatUserInfoDo = mongoTemplate.findOne(oldQuery, WeChatUserInfoDo.class);
        }
      }
      String userToken = Constants.LOGIN_USER.concat(MD5Util.encrypt(code2SessionDto.getOpenid() + System.currentTimeMillis() + RandomUtils.nextInt(9999)));
      WeChatUserInfoDto userDto = new WeChatUserInfoDto();
      BeanUtils.copyProperties(weChatUserInfoDo, userDto);
      userDto.setSessionKey(code2SessionDto.getSessionKey());
      redisTemplate.opsForValue().set(userToken, JSON.toJSONString(userDto), weChatProperties.getUserTokenExpire(), TimeUnit.SECONDS);
      return userToken;
    }
  }
}
