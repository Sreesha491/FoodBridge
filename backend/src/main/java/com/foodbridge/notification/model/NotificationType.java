package com.foodbridge.notification.model;

/** Type categories for platform notifications. */
public enum NotificationType {
    /** General informational notice. */
    INFO,
    /** A donation has been posted matching user preferences. */
    DONATION_AVAILABLE,
    /** Status update on an active donation. */
    DONATION_UPDATE,
    /** Order placed or status changed. */
    ORDER_UPDATE,
    /** Delivery assignment or status change. */
    DELIVERY_UPDATE,
    /** A payment has been processed. */
    PAYMENT_UPDATE,
    /** System-wide alert or maintenance notice. */
    SYSTEM_ALERT,
    /** A new review has been received. */
    NEW_REVIEW
}
