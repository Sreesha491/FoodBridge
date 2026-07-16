package com.foodbridge.payment.model;

/** Payment methods accepted on the platform. */
public enum PaymentMethod {
    /** Cash on delivery. */
    CASH,
    /** UPI transfer (BHIM, GPay, PhonePe, etc.). */
    UPI,
    /** Net banking. */
    NET_BANKING,
    /** Credit or debit card. */
    CARD,
    /** Platform internal wallet. */
    WALLET,
    /** Free / donation (no monetary exchange). */
    FREE
}
