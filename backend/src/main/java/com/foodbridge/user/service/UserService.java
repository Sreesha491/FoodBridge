package com.foodbridge.user.service;

import com.foodbridge.common.exception.ResourceNotFoundException;
import com.foodbridge.common.exception.UserAlreadyExistsException;
import com.foodbridge.user.dto.UserRequest;
import com.foodbridge.user.dto.UserResponse;
import com.foodbridge.user.model.User;
import com.foodbridge.user.model.UserRole;
import com.foodbridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic layer for user management.
 *
 * <p>Handles CRUD operations and role management for FoodBridge user accounts.
 * Password is always BCrypt-encoded before persisting.
 * The password field is never returned in responses.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── Create ───────────────────────────────────────────────────────────

    @Transactional
    public UserResponse createUser(UserRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new UserAlreadyExistsException(normalizedEmail);
        }
        User user = User.builder()
                .name(request.getName().trim())
                .email(normalizedEmail)
                .password(request.getPassword() != null && !request.getPassword().isBlank()
                        ? passwordEncoder.encode(request.getPassword()) : null)
                .role(request.getRole())
                .phone(request.getPhone())
                .address(request.getAddress())
                .profileImageUrl(request.getProfileImageUrl())
                .active(true)
                .build();
        UserResponse response = toResponse(userRepository.save(user));
        log.info("Admin created user [{}] with role [{}]", normalizedEmail, request.getRole());
        return response;
    }

    // ─── Read ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(String id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim())
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role).stream().map(this::toResponse).toList();
    }

    // ─── Update ───────────────────────────────────────────────────────────

    @Transactional
    public UserResponse updateUser(String id, UserRequest request) {
        User user = findById(id);
        String normalizedEmail = request.getEmail().toLowerCase().trim();

        // Email change uniqueness check
        if (!user.getEmail().equals(normalizedEmail) &&
                userRepository.existsByEmail(normalizedEmail)) {
            throw new UserAlreadyExistsException(normalizedEmail);
        }

        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setRole(request.getRole());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setProfileImageUrl(request.getProfileImageUrl());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        log.info("User [{}] updated", id);
        return toResponse(userRepository.save(user));
    }

    // ─── Delete ───────────────────────────────────────────────────────────

    @Transactional
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
        log.info("User [{}] deleted", id);
    }

    // ─── Toggle Active ────────────────────────────────────────────────────

    @Transactional
    public UserResponse toggleUserActive(String id) {
        User user = findById(id);
        user.setActive(!user.isActive());
        log.info("User [{}] active status toggled to [{}]", id, user.isActive());
        return toResponse(userRepository.save(user));
    }

    // ─── Private Helpers ──────────────────────────────────────────────────

    private User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .phone(user.getPhone())
                .address(user.getAddress())
                .profileImageUrl(user.getProfileImageUrl())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
