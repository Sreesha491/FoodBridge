package com.foodbridge.order.model;

/** Lifecycle status of an order. */
public enum OrderStatus {
    /** Order placed and awaiting confirmation. */
    PENDING,
    /** Order confirmed by the donor/restaurant. */
    CONFIRMED,
    /** Order is being prepared. */
    PREPARING,
    /** Order is out for delivery. */
    OUT_FOR_DELIVERY,
    /** Order delivered to the recipient. */
    DELIVERED,
    /** Order cancelled. */
    CANCELLED,
    /** Order refunded. */
    REFUNDED
}
