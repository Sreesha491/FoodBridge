package com.foodbridge.food.dto;

import com.foodbridge.food.model.FoodStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Response DTO for a food item listing.
 */
@Data
@Builder
public class FoodItemResponse {

    private String id;
    private String name;
    private String description;
    private String category;
    private Double quantity;
    private String unit;
    private Instant expiryDate;
    private String donorId;
    private String restaurantId;
    private FoodStatus status;
    private String imageUrl;
    private String pickupAddress;
    private String specialInstructions;
    private Instant createdAt;
    private Instant updatedAt;
}
