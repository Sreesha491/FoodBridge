package com.foodbridge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * JPA-specific configuration for FoodBridge.
 *
 * <p>Enables JPA Auditing so {@code @CreatedDate} and {@code @LastModifiedDate}
 * fields are automatically populated on persist/update.
 *
 * <p>JSR-303 Bean Validation is wired through {@link LocalValidatorFactoryBean}
 * and used by Hibernate to validate entities before writes.
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.foodbridge")
public class JpaConfig {

    /**
     * Provides the JSR-303 validator used by Hibernate's Bean Validation integration.
     */
    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }
}
