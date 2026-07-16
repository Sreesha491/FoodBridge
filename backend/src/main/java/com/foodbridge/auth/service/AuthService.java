package com.foodbridge.auth.service;

import com.foodbridge.auth.dto.AuthResponse;
import com.foodbridge.auth.dto.LoginRequest;
import com.foodbridge.auth.dto.RegisterRequest;
import com.foodbridge.common.exception.BadRequestException;
import com.foodbridge.common.exception.ResourceNotFoundException;
import com.foodbridge.common.exception.UserAlreadyExistsException;
import com.foodbridge.security.JwtProperties;
import com.foodbridge.security.JwtService;
import com.foodbridge.user.model.User;
import com.foodbridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles registration, login, token refresh, and logout logic for FoodBridge.
 *
 * <p>All write operations are wrapped in transactions to ensure atomicity.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    // ─── Register ─────────────────────────────────────────────────────────

    /**
     * Registers a new user account.
     *
     * @param request registration details
     * @return {@link AuthResponse} with a fresh JWT + refresh token
     * @throws UserAlreadyExistsException if the email is already taken
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new UserAlreadyExistsException(normalizedEmail);
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .phone(request.getPhone())
                .address(request.getAddress())
                .active(true)
                .build();

        User saved = userRepository.save(user);
        log.info("Registered new user [{}] with role [{}]", saved.getEmail(), saved.getRole());

        UserDetails userDetails = userDetailsService.loadUserByUsername(saved.getEmail());
        String accessToken = jwtService.generateToken(userDetails);
        com.foodbridge.auth.model.RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(saved.getId());

        return buildAuthResponse(accessToken, refreshToken.getToken(), saved);
    }

    // ─── Login ────────────────────────────────────────────────────────────

    /**
     * Authenticates a user and returns a fresh JWT + refresh token.
     *
     * @param request login credentials
     * @return {@link AuthResponse} with a fresh JWT token
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();

        // Spring Security validates credentials – throws BadCredentialsException on failure
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", normalizedEmail));

        if (!user.isActive()) {
            throw new BadRequestException("Your account has been deactivated. Please contact support.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateToken(userDetails);
        com.foodbridge.auth.model.RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user.getId());

        log.info("User [{}] logged in successfully", user.getEmail());
        return buildAuthResponse(accessToken, refreshToken.getToken(), user);
    }

    // ─── Refresh Token ────────────────────────────────────────────────────

    /**
     * Issues a new access token using a valid refresh token.
     *
     * @param request contains the refresh token string
     * @return {@link AuthResponse} with a new access token
     */
    @Transactional
    public AuthResponse refreshToken(com.foodbridge.auth.dto.RefreshTokenRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(com.foodbridge.auth.model.RefreshToken::getUser)
                .map(user -> {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                    String accessToken = jwtService.generateToken(userDetails);
                    log.debug("Token refreshed for user [{}]", user.getEmail());
                    return buildAuthResponse(accessToken, request.getRefreshToken(), user);
                })
                .orElseThrow(() -> new BadRequestException("Refresh token not found. Please log in again."));
    }

    // ─── Logout ───────────────────────────────────────────────────────────

    /**
     * Invalidates the user's refresh token (logout).
     * The access token remains valid until it expires (stateless JWT – cannot be revoked).
     *
     * @param userEmail the authenticated user's email
     */
    @Transactional
    public void logout(String userEmail) {
        userRepository.findByEmail(userEmail).ifPresent(user -> {
            refreshTokenService.deleteByUserId(user.getId());
            log.info("User [{}] logged out – refresh token invalidated", userEmail);
        });
    }

    // ─── Private Helpers ──────────────────────────────────────────────────

    private AuthResponse buildAuthResponse(String token, String refreshToken, User user) {
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpirationMs())
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
