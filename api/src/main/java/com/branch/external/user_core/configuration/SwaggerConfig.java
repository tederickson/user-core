package com.branch.external.user_core.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI userCoreOpenAPI() {
        final var info = new Info();
        info.setTitle("GitHub Consolidated User API");
        info.setVersion("0.0.4");
        info.setDescription("REST API for Core-User application");
        return new OpenAPI().info(info);
    }

}

