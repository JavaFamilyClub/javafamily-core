package club.javafamily.autoconfigre.resttemplate.config;

import club.javafamily.autoconfigre.resttemplate.properties.HttpClientProperties;
import club.javafamily.autoconfigre.resttemplate.properties.ProxyConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StringUtils;

import javax.net.ssl.*;
import java.net.Authenticator;
import java.net.Proxy;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Jack Li
 * @date 2022/8/12 下午6:22
 * @description
 */
@Configuration
@ConditionalOnClass(CloseableHttpClient.class)
public class RestTemplateHttpClientConfig {

   private final HttpClientProperties httpClientProperties;
   private final ObjectMapper objectMapper;

   public RestTemplateHttpClientConfig(HttpClientProperties httpClientProperties,
                                       ObjectMapper objectMapper)
   {
      this.httpClientProperties = httpClientProperties;
      this.objectMapper = objectMapper;
   }

   @Bean
   @ConditionalOnMissingBean
   public HttpClient httpClient() {
      HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

      try {
         ProxyConfig proxyConfig = httpClientProperties.getProxy();

         // 代理配置
         if(proxyConfig != null) {
            // 配置了代理
            if(proxyConfig.getType() != null && proxyConfig.getType() != Proxy.Type.DIRECT) {

               final HttpHost proxy = new HttpHost(
                  proxyConfig.getHost(),
                  proxyConfig.getPort(),
                  proxyConfig.getSchema());

               httpClientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(proxy) {

                  @Override
                  public HttpHost determineProxy(HttpHost target,
                                                 HttpRequest request, HttpContext context)
                     throws HttpException
                  {
                     if (target.getHostName().equals(proxyConfig.getHost())) {
                        return null;
                     }

                     return super.determineProxy(target, request, context);
                  }
               }).build();

               // 配置认证信息
               if(StringUtils.hasText(proxyConfig.getUserName())
                  || StringUtils.hasText(proxyConfig.getPassword()))
               {
                  Authenticator.setDefault(new ProxyNamePwdAuthenticator(
                     proxyConfig.getUserName(), proxyConfig.getPassword()));
               }
            }
         }

         SSLContext sslContext = SSLContext.getInstance("TLS");

         TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
               @Override
               public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
               }

               @Override
               public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
               }

               @Override
               public X509Certificate[] getAcceptedIssuers() {
                  return new X509Certificate[0];
               }
            }
         };
//
         sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

         httpClientBuilder.setSSLContext(sslContext);
         HostnameVerifier hostnameVerifier
            = SSLConnectionSocketFactory.getDefaultHostnameVerifier();
         SSLConnectionSocketFactory sslConnectionSocketFactory
            = new SocksSslConnectionSocketFactory(sslContext, hostnameVerifier, proxyConfig);

         Registry<ConnectionSocketFactory> socketFactoryRegistry
            = RegistryBuilder.<ConnectionSocketFactory>create()
            // 注册http和https请求
            .register("http", new SocksPlainConnectionSocketFactory(proxyConfig))
            .register("https", sslConnectionSocketFactory).build();

         PoolingHttpClientConnectionManager poolingHttpClientConnectionManager
            = getPoolingHttpClientConnectionManager(socketFactoryRegistry);

         //配置连接池
         httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);

         if(httpClientProperties.getConnManagerShared() !=  null) {
            httpClientBuilder.setConnectionManagerShared(
               httpClientProperties.getConnManagerShared());
         }

         // 重试次数
         httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(
            httpClientProperties.getRetryTimes(), true));

         //设置默认请求头
         List<Header> headers = getDefaultHeaders();
         httpClientBuilder.setDefaultHeaders(headers);
         //设置长连接保持策略
         httpClientBuilder.setKeepAliveStrategy(connectionKeepAliveStrategy());

         return httpClientBuilder.build();
      }
      catch(Exception e) {
         LOGGER.error("Init HTTP Client Error.", e);
      }

      return null;
   }

   /**
    * 设置请求头
    */
   private List<Header> getDefaultHeaders() {
      List<Header> headers = new ArrayList<>();
      headers.add(new BasicHeader("User-Agent",
         "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36"));
      headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
      headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.9"));
      headers.add(new BasicHeader("Connection", "keep-alive"));

      if(StringUtils.hasText(httpClientProperties.getContentType())) {
         headers.add(new BasicHeader("Content-Type", httpClientProperties.getContentType()));
      }

      if(StringUtils.hasText(httpClientProperties.getAccept())) {
         headers.add(new BasicHeader("Accept", httpClientProperties.getAccept()));
      }

      return headers;
   }

   /**
    * 创建连接池
    * @param socketFactoryRegistry Registry
    * @return PoolingHttpClientConnectionManager
    */
   private PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager(
      Registry<ConnectionSocketFactory> socketFactoryRegistry)
   {
      if (httpClientProperties.getMaxTotalConnect() <= 0) {
         throw new IllegalArgumentException("invalid maxTotalConnection: "
            + httpClientProperties.getMaxTotalConnect());
      }
      if (httpClientProperties.getMaxConnectPerRoute() <= 0) {
         throw new IllegalArgumentException("invalid maxConnectionPerRoute: "
            + httpClientProperties.getMaxConnectPerRoute());
      }

      //使用Httpclient连接池的方式配置(推荐)，同时支持netty，okHttp以及其他http框架
      PoolingHttpClientConnectionManager poolingHttpClientConnectionManager
         = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
      // 最大连接数
      poolingHttpClientConnectionManager.setMaxTotal(
         httpClientProperties.getMaxTotalConnect());
      // 同路由并发数
      poolingHttpClientConnectionManager.setDefaultMaxPerRoute(
         httpClientProperties.getMaxConnectPerRoute());

      if(httpClientProperties.getCloseIdleMs() != null
         && httpClientProperties.getCloseIdleMs() >= 0)
      {
         poolingHttpClientConnectionManager.closeIdleConnections(
            httpClientProperties.getCloseIdleMs(), TimeUnit.MILLISECONDS);
         poolingHttpClientConnectionManager.closeExpiredConnections();
      }

      return poolingHttpClientConnectionManager;
   }

   @Bean
   @ConditionalOnMissingBean
   public ClientHttpRequestFactory clientHttpRequestFactory() {
      HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
         = new HttpComponentsClientHttpRequestFactory(httpClient());
      // 连接超时
      clientHttpRequestFactory.setConnectTimeout(httpClientProperties.getConnectTimeout());
      // 数据读取超时时间，即SocketTimeout
      clientHttpRequestFactory.setReadTimeout(httpClientProperties.getReadTimeout());
      // 从连接池获取请求连接的超时时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
      clientHttpRequestFactory.setConnectionRequestTimeout(
         httpClientProperties.getConnectionRequestTimout());

      return clientHttpRequestFactory;
   }

   /**
    * 配置长连接保持策略
    */
   public ConnectionKeepAliveStrategy connectionKeepAliveStrategy(){
      return (response, context) -> {
         // 'keep-alive' header
         HeaderElementIterator it = new BasicHeaderElementIterator(
            response.headerIterator(HTTP.CONN_KEEP_ALIVE));
         try {
            while (it.hasNext()) {
               HeaderElement he = it.nextElement();
               LOGGER.info("HeaderElement:{}", objectMapper.writeValueAsString(he));

               String param = he.getName();
               String value = he.getValue();
               if (value != null && "timeout".equalsIgnoreCase(param)) {
                  return Long.parseLong(value) * 1000;
               }
            }
         }
         catch(Exception e) {
            LOGGER.error("解析长连接过期时间异常", e);
         }

         HttpHost target = (HttpHost) context.getAttribute(
            HttpClientContext.HTTP_TARGET_HOST);
         //如果请求目标地址,单独配置了长连接保持时间,使用该配置
         Optional<Map.Entry<String, Integer>> any =
            Optional.ofNullable(httpClientProperties.getKeepAliveTargetHost())
               .orElseGet(HashMap::new)
               .entrySet()
               .stream()
               .filter(e -> e.getKey().equalsIgnoreCase(target.getHostName())).findAny();

         //否则使用默认长连接保持时间
         return any.map(en -> en.getValue() * 1000L)
            .orElse(httpClientProperties.getKeepAliveTime() * 1000L);
      };
   }

   private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateHttpClientConfig.class);
}
