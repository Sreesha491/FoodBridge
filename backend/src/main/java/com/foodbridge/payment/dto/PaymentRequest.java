package com.foodbridge.payment.dto;

import com.foodbridge.payment.model.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for initiating a payment.
 */
@Data
public class PaymentRequest {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", message = "Amount must be non-negative")
    private Double amount;

    private String currency = "INR";

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    private String transactionId;
    private String gateway;
}
