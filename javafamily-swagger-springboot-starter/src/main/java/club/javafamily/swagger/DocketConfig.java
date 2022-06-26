package club.javafamily.swagger;

import club.javafamily.swagger.properties.SwaggerConfigProperties;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jack Li
 * @date 2021/7/26 11:58 上午
 * @description
 */
@Configuration
public class DocketConfig {
   private SwaggerConfigProperties swaggerProperties;

   @Value("${spring.application.name:Restful Apis Document}")
   private String appName;

   public DocketConfig(SwaggerConfigProperties swaggerProperties) {
      this.swaggerProperties = swaggerProperties;
   }

   @Bean
   public Docket createRestApi() {
      // 文档的基础信息配置
      Docket builder = new Docket(DocumentationType.SWAGGER_2)
         .host(swaggerProperties.getHost())
         .apiInfo(apiInfo(swaggerProperties));

      // 要忽略的参数类型
      Class<?>[] array = new Class[swaggerProperties.getIgnoredParameterTypes().size()];
      Class<?>[] ignoredParameterTypes = swaggerProperties.getIgnoredParameterTypes().toArray(array);
      builder.ignoredParameterTypes(ignoredParameterTypes);

      // 设置全局参数
      if (swaggerProperties.getGlobalOperationParameters() != null) {
         builder.globalOperationParameters(globalRequestParameters(swaggerProperties));
      }

      // 需要生成文档的接口目标配置
      Docket docket = builder.select()
         // 通过扫描包选择接口
         .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
         // 通过路径匹配选择接口
         .paths(paths(swaggerProperties))
         .build();

      return docket;
   }

   /**
    * 全局请求参数
    *
    * @param swaggerProperties {@link SwaggerConfigProperties}
    * @return RequestParameter {@link Parameter}
    */
   private List<Parameter> globalRequestParameters(SwaggerConfigProperties swaggerProperties) {
      return swaggerProperties.getGlobalOperationParameters().stream()
         .map(param -> new ParameterBuilder()
            .name(param.getName())
            .description(param.getDescription())
            .parameterType(param.getParameterType())
            .required(param.getRequired())
            .defaultValue(param.getDefaultValue())
            .modelRef(new ModelRef(param.getModelRef()))
            .build())
         .collect(Collectors.toList());
   }

   /**
    * API接口路径选择
    *
    * @param swaggerProperties
    * @return
    */

   private Predicate paths(SwaggerConfigProperties swaggerProperties) {
      // base-path处理
      // 当没有配置任何path的时候，解析/**
      if (swaggerProperties.getBasePath().isEmpty()) {
         swaggerProperties.getBasePath().add("/**");
      }

      List<com.google.common.base.Predicate<String>> basePath = new ArrayList<>();

      for (String path : swaggerProperties.getBasePath()) {
         basePath.add(PathSelectors.ant(path));
      }

      // exclude-path处理
      List<com.google.common.base.Predicate<String>> excludePath = new ArrayList<>();

      for (String path : swaggerProperties.getExcludePath()) {
         excludePath.add(PathSelectors.ant(path));
      }

      return Predicates.and(
         Predicates.not(Predicates.or(excludePath)),
         Predicates.or(basePath)
      );
   }

   /**
    * API文档基本信息
    *
    * @param swaggerProperties
    * @return
    */
   private ApiInfo apiInfo(SwaggerConfigProperties swaggerProperties) {
      ApiInfo apiInfo = new ApiInfoBuilder()
         .title(ObjectUtils.isEmpty(swaggerProperties.getTitle())
            ? appName
            : swaggerProperties.getTitle())
         .description(swaggerProperties.getDescription())
         .version(swaggerProperties.getVersion())
         .license(swaggerProperties.getLicense())
         .licenseUrl(swaggerProperties.getLicenseUrl())
         .contact(new Contact(swaggerProperties.getContact().getName(),
            swaggerProperties.getContact().getUrl(),
            swaggerProperties.getContact().getEmail()))
         .termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
         .build();
      return apiInfo;
   }
}
