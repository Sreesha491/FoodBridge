package com.foodbridge.donation.model;

/** Lifecycle status of a food donation. */
public enum DonationStatus {
    /** Donation created and awaiting confirmation. */
    PENDING,
    /** Confirmed and scheduled for pickup. */
    CONFIRMED,
    /** Picked up by delivery partner. */
    PICKED_UP,
    /** Delivered to the recipient. */
    DELIVERED,
    /** Cancelled before pickup. */
    CANCELLED
}
