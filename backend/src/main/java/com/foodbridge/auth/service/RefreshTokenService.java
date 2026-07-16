package com.foodbridge.auth.service;

import com.foodbridge.auth.model.RefreshToken;
import com.foodbridge.auth.repository.RefreshTokenRepository;
import com.foodbridge.common.exception.BadRequestException;
import com.foodbridge.security.JwtProperties;
import com.foodbridge.user.model.User;
import com.foodbridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages creation, validation, and deletion of refresh tokens.
 *
 * <p>Each user may have at most one active refresh token at a time.
 * Creating a new token automatically deletes the old one (atomic within a transaction).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    /**
     * Creates (or replaces) a refresh token for the given user.
     * The delete + save is atomic within a single transaction.
     *
     * @param userId the user's UUID
     * @return the newly created {@link RefreshToken}
     */
    @Transactional
    public RefreshToken createRefreshToken(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found with ID: " + userId));

        // Delete any existing refresh token for this user atomically
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(jwtProperties.getRefreshExpirationMs()))
                .build();

        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        log.debug("Created refresh token for user [{}], expires at [{}]", user.getEmail(), saved.getExpiryDate());
        return saved;
    }

    /**
     * Finds a refresh token by its token string.
     *
     * @param token the token value
     * @return an Optional containing the refresh token if found
     */
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Verifies the token has not expired.
     * If expired, deletes it and throws an exception.
     *
     * @param token the refresh token to verify
     * @return the token if valid
     * @throws BadRequestException if the token is expired
     */
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            log.warn("Expired refresh token detected and deleted for user [{}]",
                    token.getUser().getEmail());
            throw new BadRequestException(
                    "Refresh token has expired. Please log in again.");
        }
        return token;
    }

    /**
     * Deletes the refresh token for the given user (used on logout).
     *
     * @param userId the user's UUID
     */
    @Transactional
    public void deleteByUserId(String userId) {
        userRepository.findById(userId).ifPresent(user -> {
            refreshTokenRepository.deleteByUser(user);
            log.debug("Deleted refresh token for user [{}]", user.getEmail());
        });
    }
}
