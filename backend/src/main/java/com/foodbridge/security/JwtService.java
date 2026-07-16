package com.foodbridge.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service responsible for generating, parsing, and validating JWT tokens.
 *
 * <p>Uses JJWT 0.12 with HMAC-SHA256 signing. The signing key is derived from
 * the secret configured in {@link JwtProperties}.
 *
 * <p>Token payload includes:
 * <ul>
 *   <li>{@code sub} – user email (the Spring Security username)</li>
 *   <li>{@code roles} – comma-separated granted authorities (e.g. {@code ROLE_DONOR})</li>
 *   <li>{@code iat} – issued-at timestamp</li>
 *   <li>{@code exp} – expiration timestamp</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    // ─── Token Generation ─────────────────────────────────────────────────

    /**
     * Generates a JWT for the given {@link UserDetails}, embedding roles as a claim.
     *
     * @param userDetails the authenticated user
     * @return signed JWT string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Embed roles so the frontend can read the role without a separate API call
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("roles", roles);
        return generateToken(claims, userDetails);
    }

    /**
     * Generates a JWT with additional custom claims embedded in the payload.
     *
     * @param extraClaims additional key-value pairs to include in the token body
     * @param userDetails the authenticated user (email used as subject)
     * @return signed JWT string
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMs()))
                .signWith(getSigningKey())
                .compact();
    }

    // ─── Token Validation ─────────────────────────────────────────────────

    /**
     * Validates that the token belongs to the given user and is not expired.
     *
     * @param token       the JWT string to validate
     * @param userDetails the user to check against
     * @return {@code true} if valid
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    // ─── Claims Extraction ────────────────────────────────────────────────

    /** Extracts the subject (email / username) from the token. */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Extracts the expiration date from the token. */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /** Extracts the roles claim from the token. */
    public String extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", String.class));
    }

    /** Generic claim extractor using a resolver function. */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ─── Private Helpers ──────────────────────────────────────────────────

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
