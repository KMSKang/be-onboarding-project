package com.survey.www.commons.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "구독 서비스 API 명세서"
                   , description = "스프링 기반 구독 서비스 API 명세서"
                   , version = "v1.0.0"))
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi groupedOpenApi() {
        String[] paths = {"/api/**"};

        return GroupedOpenApi.builder()
                             .group("구독 서비스 API v1.0.0")
                             .pathsToMatch(paths)
                             .build();
    }
}
