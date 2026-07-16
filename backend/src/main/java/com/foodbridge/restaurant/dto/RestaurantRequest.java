package com.foodbridge.restaurant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for creating or updating a restaurant profile.
 */
@Data
public class RestaurantRequest {

    @NotBlank(message = "Restaurant name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    private List<String> cuisineTypes;

    private String phone;

    @Email(message = "Please provide a valid email address")
    private String email;

    private String logoUrl;
    private String description;
}
