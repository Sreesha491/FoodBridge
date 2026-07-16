package com.foodbridge.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long id;
    private String foodItemId;
    private String foodItemName;
    private Double quantity;
    private String unit;
    private Double unitPrice;
}
