package com.foodbridge.auth.controller;

import com.foodbridge.auth.dto.AuthResponse;
import com.foodbridge.auth.dto.LoginRequest;
import com.foodbridge.auth.dto.RegisterRequest;
import com.foodbridge.auth.dto.RefreshTokenRequest;
import com.foodbridge.auth.service.AuthService;
import com.foodbridge.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller providing authentication endpoints.
 *
 * <ul>
 *   <li>POST /auth/register – create a new account</li>
 *   <li>POST /auth/login   – obtain JWT and refresh token</li>
 *   <li>POST /auth/refresh – exchange refresh token for new access token</li>
 *   <li>POST /auth/logout  – invalidate refresh token</li>
 * </ul>
 *
 * <p>Register, login, and refresh are publicly accessible (no JWT required).
 * Logout requires a valid JWT.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, refresh token, and logout")
public class AuthController {

    private final AuthService authService;

    // ─── POST /auth/register ──────────────────────────────────────────────

    @PostMapping("/register")
    @SecurityRequirements  // public endpoint
    @Operation(
            summary = "Register a new user",
            description = """
                    Creates a new FoodBridge account and returns a JWT access token plus a refresh token.
                    
                    **Available roles:** `ADMIN`, `DONOR`, `RESTAURANT`, `NGO`, `DELIVERY_PARTNER`
                    
                    Use the returned `token` as a Bearer token in subsequent requests.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "message": "User registered successfully.",
                                      "data": {
                                        "token": "eyJhbGciOiJIUzI1NiJ9...",
                                        "tokenType": "Bearer",
                                        "expiresIn": 86400000,
                                        "refreshToken": "uuid-refresh-token",
                                        "userId": "uuid-user-id",
                                        "name": "John Doe",
                                        "email": "john@example.com",
                                        "role": "DONOR"
                                      },
                                      "timestamp": "2024-01-15T10:30:00Z"
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Validation error"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = "Email already registered")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully.", authResponse));
    }

    // ─── POST /auth/login ─────────────────────────────────────────────────

    @PostMapping("/login")
    @SecurityRequirements  // public endpoint
    @Operation(
            summary = "Login and obtain JWT",
            description = """
                    Authenticates with email and password. Returns a JWT access token and refresh token.
                    
                    1. Copy the `token` value from the response
                    2. Click **Authorize** at the top of this page
                    3. Paste: `Bearer <your-token>`
                    4. Call any protected endpoint
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "message": "Login successful.",
                                      "data": {
                                        "token": "eyJhbGciOiJIUzI1NiJ9...",
                                        "tokenType": "Bearer",
                                        "expiresIn": 86400000,
                                        "refreshToken": "uuid-refresh-token",
                                        "userId": "uuid-user-id",
                                        "name": "John Doe",
                                        "email": "john@example.com",
                                        "role": "DONOR"
                                      },
                                      "timestamp": "2024-01-15T10:30:00Z"
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Invalid email or password"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful.", authResponse));
    }

    // ─── POST /auth/refresh ───────────────────────────────────────────────

    @PostMapping("/refresh")
    @SecurityRequirements  // public endpoint (uses refresh token, not access token)
    @Operation(
            summary = "Refresh access token",
            description = "Exchange a valid refresh token for a new JWT access token. " +
                    "Use this when the access token expires (every 24 hours)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Token refreshed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully.", authResponse));
    }

    // ─── POST /auth/logout ────────────────────────────────────────────────

    @PostMapping("/logout")
    @Operation(
            summary = "Logout (invalidate refresh token)",
            description = """
                    Invalidates the user's refresh token server-side.
                    
                    **Note:** The access token remains valid until it expires (JWT is stateless).
                    The frontend should also delete the stored token from localStorage/cookies.
                    
                    Requires a valid Bearer token in the Authorization header.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Logged out successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized – invalid or missing JWT")
    })
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully."));
    }
}
