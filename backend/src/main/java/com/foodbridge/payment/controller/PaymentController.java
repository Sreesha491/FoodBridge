package com.foodbridge.payment.controller;

import com.foodbridge.common.response.ApiResponse;
import com.foodbridge.payment.dto.PaymentRequest;
import com.foodbridge.payment.dto.PaymentResponse;
import com.foodbridge.payment.model.PaymentStatus;
import com.foodbridge.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for payment management.
 * Base path: /v1/payments
 */
@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Manage payment records and status transitions")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all payments (ADMIN only)")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {
        return ResponseEntity.ok(ApiResponse.success("Payments retrieved.", paymentService.getAllPayments()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Payment retrieved.", paymentService.getPaymentById(id)));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment for a specific order")
    public ResponseEntity<ApiResponse<PaymentResponse>> getByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(ApiResponse.success("Payment retrieved.", paymentService.getPaymentByOrderId(orderId)));
    }

    @GetMapping("/payer/{payerId}")
    @Operation(summary = "Get payments by payer ID")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getByPayer(@PathVariable String payerId) {
        return ResponseEntity.ok(ApiResponse.success("Payments retrieved.", paymentService.getPaymentsByPayer(payerId)));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get payments by status (ADMIN only)")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getByStatus(@PathVariable PaymentStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Payments retrieved.", paymentService.getPaymentsByStatus(status)));
    }

    @PostMapping
    @Operation(summary = "Initiate a payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment initiated.", paymentService.initiatePayment(request, userDetails.getUsername())));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update payment status (ADMIN only)")
    public ResponseEntity<ApiResponse<PaymentResponse>> updateStatus(
            @PathVariable String id,
            @RequestParam PaymentStatus status,
            @RequestParam(required = false) String failureReason) {
        return ResponseEntity.ok(ApiResponse.success("Payment status updated.",
                paymentService.updateStatus(id, status, failureReason)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a payment record (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable String id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok(ApiResponse.success("Payment deleted."));
    }
}
