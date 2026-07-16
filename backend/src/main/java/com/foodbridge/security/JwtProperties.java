package com.foodbridge.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds JWT configuration from {@code application.yml} under the {@code app.jwt} prefix.
 *
 * <p>Example configuration:
 * <pre>
 * app:
 *   jwt:
 *     secret: your-secret-key-min-32-chars
 *     expiration-ms: 86400000       # 24 hours
 *     refresh-expiration-ms: 604800000  # 7 days
 * </pre>
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /** HMAC-SHA secret key (plain text, minimum 32 chars recommended for HS256). */
    private String secret;

    /** Access token validity in milliseconds. Default: 86400000 (24 h). */
    private long expirationMs = 86_400_000L;

    /** Refresh token validity in milliseconds. Default: 604800000 (7 days). */
    private long refreshExpirationMs = 604_800_000L;
}
