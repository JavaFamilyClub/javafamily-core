package club.javafamily.autoconfigre.resttemplate.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;

import java.util.Map;

@ConfigurationProperties(prefix = "javafamily.http")
public class HttpClientProperties {
   /**
    * 连接池的最大连接数，0代表不限；如果取0，需要考虑连接泄露导致系统崩溃的后果
    */
   private int maxTotalConnect = 1000;
   /**
    * 每个路由的最大连接数,如果只调用一个地址,可以将其设置为最大连接数
    */
   private int maxConnectPerRoute = 200;
   /**
    * 指客户端和服务器建立连接的超时时间,ms , 最大约21秒,因为内部tcp在进行三次握手建立连接时,默认tcp超时时间是20秒
    */
   private int connectTimeout = 20 * 1000;
   /**
    * 指客户端从服务器读取数据包的间隔超时时间,不是总读取时间,也就是socket timeout, 单位ms. 默认 10 s
    */
   private int readTimeout = 10 * 1000;

   /**
    * 指客户端从服务器读取数据包的间隔超时时间,不是总读取时间,也就是socket timeout, 单位ms. 默认 10 s
    */
   private int writeTimeout = 10 * 1000;

   private String charset = "UTF-8";
   /**
    * 重试次数,默认 2 次
    */
   private Integer retryTimes = 2;
   /**
    * 从连接池获取连接的超时时间,不宜过长,单位ms
    */
   private int connectionRequestTimout = 200;
   /**
    * 针对不同的网址,长连接保持的存活时间,单位s,如果是频繁而持续的请求,
    * 可以设置小一点,不建议设置过大,避免大量无用连接占用内存资源
    */
   private Map<String,Integer> keepAliveTargetHost;

   /**
    * 长连接保持时间 单位s,不宜过长
    */
   private int keepAliveTime = 10;

   /**
    * 代理配置
    */
   private ProxyConfig proxy;

   /**
    * text plain mapping to json
    */
   private Boolean textPlain2Json = true;

   /**
    * Content Type 请求头
    */
   private String contentType = MediaType.APPLICATION_JSON_VALUE;

   /**
    * Accept 请求头
    */
   private String accept = "application/json, text/plain, */*";

   /**
    * 最大 idle 的数量
    */
   private Integer maxIdleCount = 5;

   /**
    * 关闭  idle  链接.单位: 豪秒
    */
   private Long closeIdleMs = 30 * 1000L;

   /**
    * 是否在多个客户端间共享 Connection Manager
    */
   private Boolean connManagerShared = true;

   public int getWriteTimeout() {
      return writeTimeout;
   }

   public void setWriteTimeout(int writeTimeout) {
      this.writeTimeout = writeTimeout;
   }

   public Integer getMaxIdleCount() {
      return maxIdleCount;
   }

   public void setMaxIdleCount(Integer maxIdleCount) {
      this.maxIdleCount = maxIdleCount;
   }

   public Boolean getConnManagerShared() {
      return connManagerShared;
   }

   public void setConnManagerShared(Boolean connManagerShared) {
      this.connManagerShared = connManagerShared;
   }

   public Long getCloseIdleMs() {
      return closeIdleMs;
   }

   public void setCloseIdleMs(Long closeIdleMs) {
      this.closeIdleMs = closeIdleMs;
   }

   public Boolean getTextPlain2Json() {
      return textPlain2Json;
   }

   public void setTextPlain2Json(Boolean textPlain2Json) {
      this.textPlain2Json = textPlain2Json;
   }

   public String getContentType() {
      return contentType;
   }

   public void setContentType(String contentType) {
      this.contentType = contentType;
   }

   public String getAccept() {
      return accept;
   }

   public void setAccept(String accept) {
      this.accept = accept;
   }

   public ProxyConfig getProxy() {
      return proxy;
   }

   public void setProxy(ProxyConfig proxy) {
      this.proxy = proxy;
   }

   public int getMaxTotalConnect() {
      return maxTotalConnect;
   }

   public void setMaxTotalConnect(int maxTotalConnect) {
      this.maxTotalConnect = maxTotalConnect;
   }

   public int getMaxConnectPerRoute() {
      return maxConnectPerRoute;
   }

   public void setMaxConnectPerRoute(int maxConnectPerRoute) {
      this.maxConnectPerRoute = maxConnectPerRoute;
   }

   public int getConnectTimeout() {
      return connectTimeout;
   }

   public void setConnectTimeout(int connectTimeout) {
      this.connectTimeout = connectTimeout;
   }

   public int getReadTimeout() {
      return readTimeout;
   }

   public void setReadTimeout(int readTimeout) {
      this.readTimeout = readTimeout;
   }

   public String getCharset() {
      return charset;
   }

   public void setCharset(String charset) {
      this.charset = charset;
   }

   public Integer getRetryTimes() {
      return retryTimes;
   }

   public void setRetryTimes(Integer retryTimes) {
      this.retryTimes = retryTimes;
   }

   public int getConnectionRequestTimout() {
      return connectionRequestTimout;
   }

   public void setConnectionRequestTimout(int connectionRequestTimout) {
      this.connectionRequestTimout = connectionRequestTimout;
   }

   public Map<String, Integer> getKeepAliveTargetHost() {
      return keepAliveTargetHost;
   }

   public void setKeepAliveTargetHost(Map<String, Integer> keepAliveTargetHost) {
      this.keepAliveTargetHost = keepAliveTargetHost;
   }

   public int getKeepAliveTime() {
      return keepAliveTime;
   }

   public void setKeepAliveTime(int keepAliveTime) {
      this.keepAliveTime = keepAliveTime;
   }
}
