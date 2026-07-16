package com.foodbridge.user.model;

/**
 * Roles assigned to FoodBridge users. Each role controls access to specific
 * endpoints via Spring Security method-level annotations.
 */
public enum UserRole {
    /** Platform administrator with full access. */
    ADMIN,
    /** Individual or organisation donating food. */
    DONOR,
    /** Restaurant contributing surplus food. */
    RESTAURANT,
    /** Non-governmental organisation receiving donations. */
    NGO,
    /** Partner responsible for physically transporting donations. */
    DELIVERY_PARTNER
}
