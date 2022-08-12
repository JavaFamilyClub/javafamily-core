package club.javafamily.autoconfigre.resttemplate.config;

import club.javafamily.autoconfigre.resttemplate.properties.HttpClientProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableConfigurationProperties(HttpClientProperties.class)
@Import({
   RestTemplateHttpClientConfig.class,
   RestTemplateOkHttpConfig.class
})
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
   public RestTemplate mainRestTemplate(ClientHttpRequestFactory factory){
      return createRestTemplate(factory);
   }

   private RestTemplate createRestTemplate(ClientHttpRequestFactory factory) {
      RestTemplate restTemplate = new RestTemplate(factory);

      if(httpClientProperties.getTextPlain2Json()) {
         restTemplate.getMessageConverters()
            .add(new TextPlainMappingJackson2HttpMessageConverter(objectMapper));
      }

      //我们采用RestTemplate内部的MessageConverter
      //重新设置StringHttpMessageConverter字符集，解决中文乱码问题
      modifyDefaultCharset(restTemplate);

      // 设置默认请求头
      restTemplate.setClientHttpRequestInitializers(Arrays.asList(
         (request) -> {
            HttpHeaders headers = request.getHeaders();

            if(StringUtils.hasText(httpClientProperties.getContentType())
               && headers.getContentType() == null)
            {
               headers.setContentType(MediaType.parseMediaType(
                  httpClientProperties.getContentType()));
            }

            if(StringUtils.hasText(httpClientProperties.getAccept())
               && CollectionUtils.isEmpty(headers.getAccept()))
            {
               headers.set(HttpHeaders.ACCEPT, httpClientProperties.getContentType());
            }
         }
         ));

      //设置错误处理器
      restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

      return restTemplate;
   }

   /**
    * 修改默认的字符集类型为utf-8
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
