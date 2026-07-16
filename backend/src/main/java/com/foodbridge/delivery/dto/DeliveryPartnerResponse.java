package com.foodbridge.delivery.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Response DTO for a delivery partner profile.
 */
@Data
@Builder
public class DeliveryPartnerResponse {

    private String id;
    private String userId;
    private String vehicleType;
    private String vehicleNumber;
    private String licenseNumber;
    private String operatingArea;
    private boolean available;
    private Double rating;
    private Integer ratingCount;
    private Integer completedDeliveries;
    private Instant createdAt;
    private Instant updatedAt;
}
