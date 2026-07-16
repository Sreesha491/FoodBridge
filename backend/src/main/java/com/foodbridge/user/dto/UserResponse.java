package com.foodbridge.user.dto;

import com.foodbridge.user.model.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Response DTO for a user – password is never included.
 */
@Data
@Builder
public class UserResponse {

    private String id;
    private String name;
    private String email;
    private UserRole role;
    private String phone;
    private String address;
    private String profileImageUrl;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
