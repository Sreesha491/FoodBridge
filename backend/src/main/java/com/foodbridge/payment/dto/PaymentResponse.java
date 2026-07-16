package com.foodbridge.payment.dto;

import com.foodbridge.payment.model.PaymentMethod;
import com.foodbridge.payment.model.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Response DTO for a payment record.
 */
@Data
@Builder
public class PaymentResponse {

    private String id;
    private String orderId;
    private String payerId;
    private String payeeId;
    private Double amount;
    private String currency;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
    private String gateway;
    private String failureReason;
    private Instant createdAt;
    private Instant updatedAt;
}
