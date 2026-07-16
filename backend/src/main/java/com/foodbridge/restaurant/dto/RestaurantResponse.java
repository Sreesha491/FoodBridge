package com.foodbridge.restaurant.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for a restaurant profile.
 */
@Data
@Builder
public class RestaurantResponse {

    private String id;
    private String name;
    private String ownerId;
    private String address;
    private String city;
    private List<String> cuisineTypes;
    private String phone;
    private String email;
    private Double rating;
    private Integer ratingCount;
    private String logoUrl;
    private String description;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
