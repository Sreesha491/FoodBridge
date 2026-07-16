package com.foodbridge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) configuration for the FoodBridge API.
 *
 * <p>Permits the React/Vite frontend (running on {@code http://localhost:5173}
 * in development) to communicate with the Spring Boot backend
 * (on {@code http://localhost:8080}).
 *
 * <p>In production, origins must be restricted to the actual deployed domain.
 * Override {@code ALLOWED_ORIGINS} via environment variable or profile-specific config.
 */
@Configuration
public class CorsConfig {

    /** Allowed origin(s) – Vite dev server + production domain placeholder. */
    private static final List<String> ALLOWED_ORIGINS = List.of(
            "http://localhost:5173",   // Vite dev server
            "http://localhost:3000",   // CRA fallback (just in case)
            "https://foodbridge.app"   // Production domain (placeholder)
    );

    private static final List<String> ALLOWED_METHODS = List.of(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
    );

    private static final List<String> ALLOWED_HEADERS = List.of(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
    );

    private static final List<String> EXPOSED_HEADERS = List.of(
            "Authorization",
            "Content-Disposition"
    );

    /**
     * Builds and registers the CORS configuration source used by both
     * {@link SecurityConfig} and the Spring MVC CORS filter.
     *
     * @return configured {@link CorsConfigurationSource}
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(ALLOWED_ORIGINS);
        config.setAllowedMethods(ALLOWED_METHODS);
        config.setAllowedHeaders(ALLOWED_HEADERS);
        config.setExposedHeaders(EXPOSED_HEADERS);
        config.setAllowCredentials(true);           // Required for JWT cookies / auth headers
        config.setMaxAge(3600L);                    // Cache preflight for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // Apply to all API routes
        return source;
    }
}
