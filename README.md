# wechat-spring-boot-starter

***
## 如何使用?
step1:下载：https://github.com/qwerfdsazxcv/wechat-spring-boot-starter.gits   
step2: maven 编译打包后，mvn install 命令上传到仓库


##Quickstart:   
step1: 项目的pom.xml 中引入： 

    <dependency>
        <groupId>com.mili.wechat</groupId>
        <artifactId>wechat-spring-boot-starter</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>

step2: 项目yaml 文件中配置：

    spring:
      data:
        mongodb:
          uri: xxx
      redis:
        database: 0
        timeout: 3000
        host: xxx
        port: xxx
        password: xxx
        pool:
          maxActive: 1000
          maxWait: -1
          maxIdle: 30
          minIdle: 10

    weixin:
      api:
        url: https://api.weixin.qq.com
        appId: xxx
        appSecret: xxx
        grantType: xxx
      loginPage:
      tokenKey: xxx
      unloginCode: NOT_LOGIN
      userTokenExpire: 7200
      service:
        enabled: true
      notFilterUrls:
        - /user/login

step3: spring-boot 项目配置登录filter:  
    
    @Bean   
    public LoginFilter loginFilter(){
		return new LoginFilter();
	}
    
step4: 编写小程序登录controller:

    @Autowired   
    private WeChatTemplate weChatTemplate;  
      @RequestMapping(path = "/user/login", method = RequestMethod.GET)
      public Map login(String code){
        Map<String, String> result=new HashMap<>();
        try{
           String userToken =weChatTemplate.weChatLogin(code);
          result.put("userToken",userToken);
        }catch (UnloginException e){
          result.put("code","123");
          result.put("message",e.getMessage());
        }
        return result;
      }
Tips：
功能集中在WeChatTemplate中
