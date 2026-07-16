package com.foodbridge.payment.model;

/** Lifecycle status of a payment record. */
public enum PaymentStatus {
    /** Payment initiated but not yet processed. */
    PENDING,
    /** Payment successfully completed. */
    COMPLETED,
    /** Payment rejected by the payment processor. */
    FAILED,
    /** Payment refunded to the payer. */
    REFUNDED,
    /** Payment on hold pending review. */
    ON_HOLD
}
