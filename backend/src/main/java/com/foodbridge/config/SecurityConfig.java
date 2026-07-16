package com.foodbridge.config;

import com.foodbridge.security.CustomAccessDeniedHandler;
import com.foodbridge.security.CustomAuthEntryPoint;
import com.foodbridge.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security configuration for FoodBridge.
 *
 * <p>Security strategy:
 * <ul>
 *   <li>CORS delegated to {@link CorsConfig#corsConfigurationSource()}</li>
 *   <li>CSRF disabled – stateless REST API with JWT</li>
 *   <li>Sessions: STATELESS – no server-side session state</li>
 *   <li>JWT filter inserted before {@link UsernamePasswordAuthenticationFilter}</li>
 *   <li>Custom 401 JSON response via {@link CustomAuthEntryPoint}</li>
 *   <li>Custom 403 JSON response via {@link CustomAccessDeniedHandler}</li>
 *   <li>Public routes: auth endpoints, Swagger UI, Actuator health, public food browsing</li>
 *   <li>All other routes require a valid JWT</li>
 *   <li>Role-based method security via {@code @PreAuthorize} annotations</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * URL patterns that are fully accessible without authentication.
     *
     * <p><strong>Note:</strong> The server context-path is {@code /api}, so a request to
     * {@code http://localhost:8081/api/auth/login} arrives at the filter as {@code /auth/login}.
     * Spring Security matches against the path <em>after</em> the context-path, so
     * {@code /auth/**} is correct here.
     */
    private static final String[] PUBLIC_URLS = {
            "/auth/**",           // registration, login, refresh, logout (needs auth but let it through – method security handles it)
            "/api-docs/**",       // OpenAPI JSON spec
            "/api-docs.yaml",     // OpenAPI YAML spec
            "/swagger-ui/**",     // Swagger UI static assets
            "/swagger-ui.html",   // Swagger UI entry page
            "/actuator/health",   // health check (no credentials needed)
            "/actuator/info"      // app info (no credentials needed)
    };

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final CustomAuthEntryPoint customAuthEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * Primary security filter chain with JWT authentication.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ── CORS ────────────────────────────────────────────────────
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            // ── CSRF disabled (stateless REST + JWT) ────────────────────
            .csrf(AbstractHttpConfigurer::disable)

            // ── Session: STATELESS ──────────────────────────────────────
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ── Authorization rules ─────────────────────────────────────
            .authorizeHttpRequests(auth -> auth
                    // Public auth endpoints
                    .requestMatchers(PUBLIC_URLS).permitAll()
                    // Public browsing: anyone can view food listings and restaurants
                    .requestMatchers(HttpMethod.GET, "/v1/food-items/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/v1/restaurants/**").permitAll()
                    // All other requests require a valid JWT
                    .anyRequest().authenticated()
            )

            // ── Custom 401 / 403 JSON responses ─────────────────────────
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(customAuthEntryPoint)
                    .accessDeniedHandler(customAccessDeniedHandler)
            )

            // ── Authentication provider ─────────────────────────────────
            .authenticationProvider(authenticationProvider())

            // ── JWT filter before username/password filter ───────────────
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * DAO authentication provider wired to our UserDetailsService and BCrypt encoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Exposes the {@link AuthenticationManager} bean so {@link com.foodbridge.auth.service.AuthService}
     * can call {@code authenticate()} during login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * BCrypt password encoder with strength 12 (industry standard for production).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
