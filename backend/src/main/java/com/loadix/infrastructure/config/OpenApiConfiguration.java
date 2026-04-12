package com.loadix.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI loadixOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LOADIX Backend API")
                        .version("0.0.1-SNAPSHOT")
                        .description("Core backend API for LOADIX."))
                .schemaRequirement("cookieAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.COOKIE)
                        .name("LOADIX_AUTH"))
                .addSecurityItem(new SecurityRequirement().addList("cookieAuth"));
    }
}
