package com.foodbridge.common.exception;

import com.foodbridge.common.response.ApiResponse;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the FoodBridge REST API.
 *
 * <p>Catches exceptions thrown anywhere in the controller layer and
 * transforms them into a consistent {@link ApiResponse} JSON structure,
 * so clients never receive raw Spring error pages.
 *
 * <p>Handlers:
 * <ul>
 *   <li>{@link MethodArgumentNotValidException} – Bean Validation failures (400)</li>
 *   <li>{@link BadRequestException} – Business rule violations (400)</li>
 *   <li>{@link BadCredentialsException} – Invalid login credentials (401)</li>
 *   <li>{@link JwtException} – Invalid / expired JWT (401)</li>
 *   <li>{@link DisabledException} – Account disabled (401)</li>
 *   <li>{@link ResourceNotFoundException} – Entity not found (404)</li>
 *   <li>{@link UserAlreadyExistsException} – Duplicate email (409)</li>
 *   <li>{@link AccessDeniedException} – Insufficient permissions (403)</li>
 *   <li>{@link IllegalArgumentException} – Bad client input (400)</li>
 *   <li>{@link MethodArgumentTypeMismatchException} – Type mismatch (400)</li>
 *   <li>{@link Exception} – Unhandled catch-all (500)</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─── 400 Bad Request: Bean Validation ────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult()
          .getAllErrors()
          .forEach(error -> {
              String fieldName = (error instanceof FieldError fe)
                      ? fe.getField()
                      : error.getObjectName();
              fieldErrors.put(fieldName, error.getDefaultMessage());
          });

        log.warn("Validation failed for request [{}]: {}", request.getDescription(false), fieldErrors);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("Validation failed. Please check the provided fields.", fieldErrors.toString()));
    }

    // ─── 400 Bad Request: Business Rule / Illegal Argument ───────────────

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(
            BadRequestException ex, WebRequest request) {

        log.warn("Bad request [{}]: {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ex.getMessage(), "Bad request"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {

        log.warn("Illegal argument [{}]: {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("Invalid request parameter.", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        String message = String.format(
                "Parameter '%s' should be of type '%s'.",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        log.warn("Type mismatch: {}", message);
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("Type mismatch in request parameter.", message));
    }

    // ─── 401 Unauthorized: Authentication Failures ───────────────────────

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid email or password.", "Authentication failed"));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwtException(JwtException ex) {
        log.warn("JWT error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid or expired JWT token.", ex.getMessage()));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Void>> handleDisabled(DisabledException ex) {
        log.warn("Disabled account: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Your account has been disabled. Please contact support.", "Account disabled"));
    }

    // ─── 403 Forbidden ────────────────────────────────────────────────────

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied [{}]: {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("You do not have permission to access this resource.", "Access denied"));
    }

    // ─── 404 Not Found ────────────────────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {

        log.warn("Resource not found [{}]: {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), "Resource not found"));
    }

    // ─── 409 Conflict ─────────────────────────────────────────────────────

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExists(
            UserAlreadyExistsException ex, WebRequest request) {

        log.warn("User already exists [{}]: {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage(), "Conflict"));
    }

    // ─── 500 Internal Server Error: Catch-all ─────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("Unhandled exception for request [{}]: {}",
                request.getDescription(false), ex.getMessage(), ex);

        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error(
                        "An unexpected error occurred. Please try again later.",
                        "Internal server error"));
    }
}
