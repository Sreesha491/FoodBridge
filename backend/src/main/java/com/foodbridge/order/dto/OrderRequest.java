package com.foodbridge.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for placing or updating an order.
 */
@Data
public class OrderRequest {

    @NotBlank(message = "Donor ID is required")
    private String donorId;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemDto> items;

    private Double totalAmount;
    private String deliveryAddress;
    private String instructions;
}
