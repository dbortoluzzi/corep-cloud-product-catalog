package com.corep.productcatalog.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) configuration
 * Provides interactive API documentation
 * Accessible at: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productCatalogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Catalog API")
                        .description("REST API for Product Catalog Management System - Cloud Computing Course")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Corep")
                                .email("info@corep.com"))
                        .license(new License()
                                .name("Educational License")
                                .url("https://example.com")));
    }
}

