package com.loadix.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI loadixOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LOADIX Backend API")
                        .version("0.0.1-SNAPSHOT")
                        .description("Core backend API for LOADIX."));
    }
}
