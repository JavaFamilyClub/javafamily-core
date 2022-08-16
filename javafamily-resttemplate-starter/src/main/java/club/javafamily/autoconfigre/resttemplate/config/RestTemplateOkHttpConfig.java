package club.javafamily.autoconfigre.resttemplate.config;

import club.javafamily.autoconfigre.resttemplate.properties.HttpClientProperties;
import club.javafamily.autoconfigre.resttemplate.properties.ProxyConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * @author Jack Li
 * @date 2022/8/12 下午6:22
 * @description
 */
@Configuration
@ConditionalOnClass(OkHttpClient.class)
@ConditionalOnProperty(name = "javafamily.http.client-impl", havingValue = "OKHTTP3", matchIfMissing = true)
public class RestTemplateOkHttpConfig {

   private final HttpClientProperties httpClientProperties;
   private final ObjectMapper objectMapper;

   public RestTemplateOkHttpConfig(HttpClientProperties httpClientProperties,
                                   ObjectMapper objectMapper)
   {
      this.httpClientProperties = httpClientProperties;
      this.objectMapper = objectMapper;
   }

   @Bean
   @ConditionalOnMissingBean
   public ClientHttpRequestFactory httpRequestFactory() {
      return new OkHttp3ClientHttpRequestFactory(okHttpConfigClient());
   }

   public OkHttpClient okHttpConfigClient(){
      OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder()
         .connectionPool(pool())
         .connectTimeout(httpClientProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
         .readTimeout(httpClientProperties.getReadTimeout(), TimeUnit.MILLISECONDS)
         .writeTimeout(httpClientProperties.getWriteTimeout(), TimeUnit.MILLISECONDS)
         .hostnameVerifier((hostname, session) -> true);

      final ProxyConfig proxyConfig = httpClientProperties.getProxy();

      if(proxyConfig != null && proxyConfig.getType() != null) {
         clientBuilder = clientBuilder.proxy(new Proxy(
            proxyConfig.getType(), new InetSocketAddress(
               proxyConfig.getHost(), proxyConfig.getPort())));

         if(StringUtils.hasText(proxyConfig.getUserName())) {
            Authenticator authenticator = (Route route, Response response) -> {
               String credential = Credentials.basic(
                  proxyConfig.getUserName(), proxyConfig.getPassword());

               return response.request().newBuilder()
                  .header("Proxy-Authorization", credential)
                  .build();
            };

            clientBuilder.authenticator(authenticator);
            clientBuilder.proxyAuthenticator(authenticator);
         }
      }

      if(httpClientProperties.getRetryTimes() != null) {
         clientBuilder.retryOnConnectionFailure(
            httpClientProperties.getRetryTimes() > 0);
      }

      return clientBuilder.build();
   }

   public ConnectionPool pool() {
      return new ConnectionPool(httpClientProperties.getMaxIdleCount(),
         httpClientProperties.getKeepAliveTime(), TimeUnit.SECONDS);
   }

   private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateOkHttpConfig.class);
}
