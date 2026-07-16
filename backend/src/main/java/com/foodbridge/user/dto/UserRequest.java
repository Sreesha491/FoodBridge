package com.foodbridge.user.dto;

import com.foodbridge.user.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for creating or updating a user profile.
 */
@Data
public class UserRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotNull(message = "Role is required")
    private UserRole role;

    private String phone;
    private String address;
    private String profileImageUrl;

    /** Only provided when creating a user directly (admin use). */
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
