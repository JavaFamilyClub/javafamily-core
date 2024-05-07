# javafamily-resttemplate-starter
> RestTemplate Support SpringBoot starter

[![MavenPublish](https://github.com/JavaFamilyClub/javafamily-core/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/JavaFamilyClub/javafamily-core/actions/workflows/maven-publish.yml)

## 1. 引入依赖

* Maven Central Release

```xml
<dependency>
   <groupId>club.javafamily</groupId>
   <artifactId>javafamily-resttemplate-starter</artifactId>
   <version>2.3.2-beta.7</version>
</dependency>
```

* Maven Central Snapshot 仓库

``` xml
   <repositories>
      <repository>
         <id>maven-central</id>
         <url>https://oss.sonatype.org/content/repositories/snapshots</url>
         <releases>
            <enabled>false</enabled>
            <updatePolicy>never</updatePolicy>
         </releases>
         <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
         </snapshots>
      </repository>
   </repositories>

      <dependencies>
         <dependency>
            <groupId>club.javafamily</groupId>
            <artifactId>javafamily-resttemplate-starter</artifactId>
            <version>2.3.2-SNAPSHOT</version>
         </dependency>
      </dependencies>
```

## 2. 使用
> RestTemplate 开箱即用, 引入依赖, 直接注入即可

``` java
@SpringBootTest
public class RestTemplateTests {

   @Autowired
   private RestTemplate restTemplate;
```

## 3. 配置

> `javafamily-resttemplate-starter` 已经基本支持了所有配置项, 满足大部分使用场景.

### 3.1 全部配置项

 | 属性 | 类型 | 描述 | 默认值 |
 |   --   |   -   |   -----   |   --   |
 | javafamily.http.maxTotalConnect | java.lang.Integer | 连接池的最大连接数，0代表不限；如果取0，需要考虑连接泄露导致系统崩溃的后果. | 1000 |
 | javafamily.http.maxConnectPerRoute | java.lang.Integer | 每个路由的最大连接数,如果只调用一个地址,可以将其设置为最大连接数. | 200 |
 | javafamily.http.connectTimeout | java.lang.Integer | 指客户端和服务器建立连接的超时时间,ms , 最大约21秒,因为内部tcp在进行三次握手建立连接时,默认tcp超时时间是20秒. | 20000 |
 | javafamily.http.readTimeout | java.lang.Integer | 指客户端从服务器读取数据包的间隔超时时间,不是总读取时间,也就是socket timeout, 单位ms. | 30000 |
 | javafamily.http.charset | java.lang.String | 编码. | UTF-8 |
 | javafamily.http.retryTimes | java.lang.Integer | 重试次数. | 2 |
 | javafamily.http.connectionRequestTimout | java.lang.Integer | 从连接池获取连接的超时时间,不宜过长,单位ms. | 200 |
 | javafamily.http.keepAliveTargetHost | java.util.Map<java.lang.String,java.lang.Integer> | 针对不同的网址,长连接保持的存活时间,单位s. |  |
 | javafamily.http.keepAliveTime | java.lang.Integer | 长连接保持时间 单位s,不宜过长. | 10 |
 | javafamily.http.closeIdleMs | java.lang.Long | 关闭 idle 连接的时长(ms). | 30000 |
 | javafamily.http.connManagerShared | java.lang.Boolean | 是否在多个客户端间共享 Connection Manager. | true |
 | javafamily.http.textPlain2Json | java.lang.Boolean | 添加 jackson 转换器, 将 text plain mapping 转换为 json. | true |
 | javafamily.http.contentType | java.lang.String | Content Type 请求头. | application/json |
 | javafamily.http.accept | java.lang.Integer | Accept 请求头. | application/json, text/plain, \*\/\* |
 | javafamily.http.proxy.type | java.net.Proxy$Type | 代理类型. DIRECT/HTTP/SOCKS |  |
 | javafamily.http.proxy.schema | java.lang.String | 代理 schema. | http |
 | javafamily.http.proxy.host | java.lang.String | 代理主机. |  |
 | javafamily.http.proxy.port | java.lang.Integer | 代理端口. |  |
 | javafamily.http.proxy.userName | java.lang.String | 代理认证用户名. |  |
 | javafamily.http.proxy.password | java.lang.String | 代理认证密码. |  |

### 3.2 自定义

> 当然了, 如果以上配置都无法满足您的需求的话, 您也可以自定义, 因为在自动配置类中, bean 的创建都是弱注入!
> 只要您在 IOC 容器中已经创建了 `restTemplate`, 那此自动配置就不会再创建!

``` java
@Bean
@ConditionalOnMissingBean
public RestTemplate mainRestTemplate(ClientHttpRequestFactory factory){
   return createRestTemplate(factory);
}
```

