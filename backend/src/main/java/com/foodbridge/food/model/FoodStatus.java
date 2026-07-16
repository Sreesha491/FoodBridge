package com.foodbridge.food.model;

/** Lifecycle status of a food item on the platform. */
public enum FoodStatus {
    /** Newly listed, awaiting acceptance. */
    AVAILABLE,
    /** Accepted by an NGO / recipient, not yet picked up. */
    RESERVED,
    /** In transit with a delivery partner. */
    IN_TRANSIT,
    /** Successfully delivered to the recipient. */
    DELIVERED,
    /** Expired or discarded without delivery. */
    EXPIRED,
    /** Removed / cancelled by the donor. */
    CANCELLED
}
