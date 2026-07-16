package com.foodbridge.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for creating or updating a delivery partner profile.
 */
@Data
public class DeliveryPartnerRequest {

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    private String vehicleNumber;
    private String licenseNumber;
    private String operatingArea;
}
