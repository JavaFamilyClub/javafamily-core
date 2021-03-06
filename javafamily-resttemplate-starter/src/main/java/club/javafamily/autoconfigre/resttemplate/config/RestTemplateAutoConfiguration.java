package club.javafamily.autoconfigre.resttemplate.config;

import club.javafamily.autoconfigre.resttemplate.properties.HttpClientProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(value = {RestTemplate.class, CloseableHttpClient.class})
@EnableConfigurationProperties(HttpClientProperties.class)
public class RestTemplateAutoConfiguration {
   private final HttpClientProperties httpClientProperties;
   private final ObjectMapper objectMapper;

   @Autowired
   public RestTemplateAutoConfiguration(HttpClientProperties httpClientProperties,
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
         //????????????ssl??????
//         SSLContext sslContext = new SSLContextBuilder()
//            .loadTrustMaterial(null, (arg0, arg1) -> true).build();

         SSLContext sslContext = SSLContext.getInstance("SSL");

         TrustManager[] trustAllCerts =new TrustManager[] {
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

         sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

         httpClientBuilder.setSslcontext(sslContext);
         AllowAllHostnameVerifier hostnameVerifier = new AllowAllHostnameVerifier();
         SSLConnectionSocketFactory sslConnectionSocketFactory
            = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
         Registry<ConnectionSocketFactory> socketFactoryRegistry
            = RegistryBuilder.<ConnectionSocketFactory>create()
            // ??????http???https??????
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", sslConnectionSocketFactory).build();

         //??????Httpclient????????????????????????(??????)???????????????netty???okHttp????????????http??????
         PoolingHttpClientConnectionManager poolingHttpClientConnectionManager
            = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
         // ???????????????
         poolingHttpClientConnectionManager.setMaxTotal(httpClientProperties.getMaxTotalConnect());
         // ??????????????????
         poolingHttpClientConnectionManager.setDefaultMaxPerRoute(
            httpClientProperties.getMaxConnectPerRoute());
         //???????????????
         httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);
         // ????????????
         httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(
            httpClientProperties.getRetryTimes(), true));

         //?????????????????????
         List<Header> headers = getDefaultHeaders();
         httpClientBuilder.setDefaultHeaders(headers);
         //???????????????????????????
         httpClientBuilder.setKeepAliveStrategy(connectionKeepAliveStrategy());

         return httpClientBuilder.build();
      }
      catch(Exception e) {
         LOGGER.error("Init HTTP Client Error.", e);
      }

      return null;
   }

   @Bean
   @ConditionalOnMissingBean
   public ClientHttpRequestFactory clientHttpRequestFactory() {
      if (httpClientProperties.getMaxTotalConnect() <= 0) {
         throw new IllegalArgumentException("invalid maxTotalConnection: "
            + httpClientProperties.getMaxTotalConnect());
      }
      if (httpClientProperties.getMaxConnectPerRoute() <= 0) {
         throw new IllegalArgumentException("invalid maxConnectionPerRoute: "
            + httpClientProperties.getMaxConnectPerRoute());
      }

      HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
         = new HttpComponentsClientHttpRequestFactory(httpClient());
      // ????????????
      clientHttpRequestFactory.setConnectTimeout(httpClientProperties.getConnectTimeout());
      // ??????????????????????????????SocketTimeout
      clientHttpRequestFactory.setReadTimeout(httpClientProperties.getReadTimeout());
      // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
      clientHttpRequestFactory.setConnectionRequestTimeout(
         httpClientProperties.getConnectionRequestTimout());

      return clientHttpRequestFactory;
   }

   @Bean
   @ConditionalOnMissingBean
   public RestTemplate mainRestTemplate(ClientHttpRequestFactory factory){
      return createRestTemplate(factory);
   }

   /**
    * ???????????????????????????
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
            LOGGER.error("?????????????????????????????????", e);
         }

         HttpHost target = (HttpHost) context.getAttribute(
            HttpClientContext.HTTP_TARGET_HOST);
         //????????????????????????,????????????????????????????????????,???????????????
         Optional<Map.Entry<String, Integer>> any =
            Optional.ofNullable(httpClientProperties.getKeepAliveTargetHost())
               .orElseGet(HashMap::new)
               .entrySet()
               .stream()
               .filter(e -> e.getKey().equalsIgnoreCase(target.getHostName())).findAny();

         //???????????????????????????????????????
         return any.map(en -> en.getValue() * 1000L)
            .orElse(httpClientProperties.getKeepAliveTime() * 1000L);
      };
   }

   /**
    * ???????????????
    */
   private List<Header> getDefaultHeaders() {
      List<Header> headers = new ArrayList<>();
      headers.add(new BasicHeader("User-Agent",
         "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36"));
      headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
      headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.9"));
      headers.add(new BasicHeader("Connection", "keep-alive"));
//      headers.add(new BasicHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE));
//      headers.add(new BasicHeader("Accept", "application/json, text/plain, */*"));

      return headers;
   }

   private RestTemplate createRestTemplate(ClientHttpRequestFactory factory) {
      RestTemplate restTemplate = new RestTemplate(factory);

      //????????????RestTemplate?????????MessageConverter
      //????????????StringHttpMessageConverter????????????????????????????????????
      modifyDefaultCharset(restTemplate);

      //?????????????????????
      restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

      return restTemplate;
   }

   /**
    * ?????????????????????????????????utf-8
    *
    * @param restTemplate
    */
   private void modifyDefaultCharset(RestTemplate restTemplate) {
      List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
      HttpMessageConverter<?> converterTarget = null;

      for(HttpMessageConverter<?> item : converterList) {
         if (StringHttpMessageConverter.class == item.getClass()) {
            converterTarget = item;
            break;
         }
      }
      if (null != converterTarget) {
         converterList.remove(converterTarget);
      }

      Charset defaultCharset = Charset.forName(httpClientProperties.getCharset());
      converterList.add(1, new StringHttpMessageConverter(defaultCharset));
   }

   private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateAutoConfiguration.class);
}
