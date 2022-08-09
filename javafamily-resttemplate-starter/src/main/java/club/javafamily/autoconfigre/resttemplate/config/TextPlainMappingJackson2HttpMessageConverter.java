package club.javafamily.autoconfigre.resttemplate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.*;

/**
 * @author Jack Li
 * @date 2022/8/9 下午4:55
 * @description
 */
public class TextPlainMappingJackson2HttpMessageConverter
   extends MappingJackson2HttpMessageConverter
{
   public TextPlainMappingJackson2HttpMessageConverter(){
      this(Jackson2ObjectMapperBuilder.json().build());
   }

   public TextPlainMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
      super(objectMapper);
      setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN,
         MediaType.APPLICATION_JSON,
         new MediaType("application", "*+json")));
   }
}
