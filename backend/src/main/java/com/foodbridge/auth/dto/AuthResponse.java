package com.foodbridge.auth.dto;

import com.foodbridge.user.model.UserRole;
import lombok.Builder;
import lombok.Data;

/**
 * Response returned after a successful register or login.
 */
@Data
@Builder
public class AuthResponse {

    /** JWT access token. */
    private String token;

    /** Token type (always "Bearer"). */
    @Builder.Default
    private String tokenType = "Bearer";

    /** Token validity in milliseconds. */
    private long expiresIn;

    /** Refresh token. */
    private String refreshToken;

    /** ID of the authenticated user. */
    private String userId;

    /** Display name. */
    private String name;

    /** Email (= username). */
    private String email;

    /** Assigned platform role. */
    private UserRole role;
}
