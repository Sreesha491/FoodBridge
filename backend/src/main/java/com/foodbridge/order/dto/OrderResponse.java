package com.foodbridge.order.dto;

import com.foodbridge.order.model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for an order.
 */
@Data
@Builder
public class OrderResponse {

    private String id;
    private String recipientId;
    private String donorId;
    private String deliveryPartnerId;
    private List<OrderItemDto> items;
    private Double totalAmount;
    private OrderStatus status;
    private String deliveryAddress;
    private String instructions;
    private Instant statusUpdatedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
