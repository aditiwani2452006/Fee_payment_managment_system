package com.mmcoe.feepay.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 / Swagger documentation configuration.
 * Demonstrates: SE (Interactive API specification & contract testing).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "BearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("MMCOE Fee Payment Management Platform API")
                        .description("RESTful Backend for College Semester Mini-Project (IT Dept) - Mid-Semester Review Build.\n" +
                                "Demonstrates OS (Concurrency locks), DBMS (14-table schema & ACID writes), CN (JWT auth), OOP & DSA.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("MMCOE IT Project Teams (1, 2, 3)")
                                .email("itprojects@mmcoe.edu.in"))
                        .license(new License().name("Academic Review Build")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token obtained from POST /api/auth/login")));
    }
}
