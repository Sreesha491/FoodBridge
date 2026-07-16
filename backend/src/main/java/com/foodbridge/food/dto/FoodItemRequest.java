package com.foodbridge.food.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

/**
 * Request DTO for creating or updating a food item listing.
 */
@Data
public class FoodItemRequest {

    @NotBlank(message = "Food name is required")
    private String name;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    private Double quantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    private Instant expiryDate;

    private String restaurantId;
    private String imageUrl;
    private String pickupAddress;
    private String specialInstructions;
}
