package com.corep.productcatalog.shared.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Type-safe configuration properties
 * Demonstrates @ConfigurationProperties for externalized configuration
 * Useful for cloud deployments where configuration comes from environment variables or config servers
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class ApplicationProperties {

    /**
     * Application name
     */
    private String name = "Product Catalog Service";

    /**
     * API version
     */
    private String version = "1.0.0";

    /**
     * Pagination defaults
     */
    private Pagination pagination = new Pagination();

    @Getter
    @Setter
    public static class Pagination {
        /**
         * Default page size
         */
        private int defaultPageSize = 10;

        /**
         * Maximum page size
         */
        private int maxPageSize = 100;
    }
}

