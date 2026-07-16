package com.foodbridge.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.Instant;

/**
 * Standardized API response wrapper used across all FoodBridge endpoints.
 *
 * <p>Every REST controller returns this envelope so that clients always
 * receive a consistent JSON structure:
 *
 * <pre>
 * {
 *   "success": true,
 *   "message": "Donation retrieved successfully",
 *   "data": { ... },
 *   "timestamp": "2024-01-15T10:30:00Z",
 *   "error": null    ← omitted when null (success path)
 * }
 * </pre>
 *
 * @param <T> the type of the {@code data} payload
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** {@code true} on success, {@code false} on error. */
    private final boolean success;

    /** Human-readable status message. */
    private final String message;

    /** The response payload (null on error responses). */
    private final T data;

    /** ISO-8601 UTC timestamp of when the response was generated. */
    private final Instant timestamp;

    /**
     * Error detail string (null on success responses).
     * Populated by {@link com.foodbridge.common.exception.GlobalExceptionHandler}.
     */
    private final String error;

    // ─── Private constructor – use factory methods below ──────────────────

    private ApiResponse(boolean success, String message, T data, String error) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
        this.timestamp = Instant.now();
    }

    // ─── Factory Methods ──────────────────────────────────────────────────

    /**
     * Creates a success response with a data payload.
     *
     * @param message descriptive message
     * @param data    the payload
     * @param <T>     payload type
     * @return success {@link ApiResponse}
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    /**
     * Creates a success response with no data payload (e.g., DELETE operations).
     *
     * @param message descriptive message
     * @param <T>     payload type (typically {@link Void})
     * @return success {@link ApiResponse} with {@code null} data
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null);
    }

    /**
     * Creates an error response.
     *
     * @param message user-facing error message
     * @param error   technical error detail
     * @param <T>     payload type
     * @return error {@link ApiResponse}
     */
    public static <T> ApiResponse<T> error(String message, String error) {
        return new ApiResponse<>(false, message, null, error);
    }
}
