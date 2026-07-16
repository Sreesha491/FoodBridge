package com.foodbridge.order.controller;

import com.foodbridge.common.response.ApiResponse;
import com.foodbridge.order.dto.OrderRequest;
import com.foodbridge.order.dto.OrderResponse;
import com.foodbridge.order.model.OrderStatus;
import com.foodbridge.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for order management.
 * Base path: /v1/orders
 */
@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Manage food orders on the platform")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get all orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved.", orderService.getAllOrders()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Order retrieved.", orderService.getOrderById(id)));
    }

    @GetMapping("/recipient/{recipientId}")
    @Operation(summary = "Get orders by recipient ID")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getByRecipient(@PathVariable String recipientId) {
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved.", orderService.getOrdersByRecipient(recipientId)));
    }

    @GetMapping("/donor/{donorId}")
    @Operation(summary = "Get orders by donor ID")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getByDonor(@PathVariable String donorId) {
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved.", orderService.getOrdersByDonor(donorId)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved.", orderService.getOrdersByStatus(status)));
    }

    @PostMapping
    @Operation(summary = "Place a new order")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed.", orderService.createOrder(request, userDetails.getUsername())));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an order")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrder(
            @PathVariable String id,
            @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Order updated.", orderService.updateOrder(id, request)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable String id,
            @RequestParam OrderStatus status,
            @RequestParam(required = false) String deliveryPartnerId) {
        return ResponseEntity.ok(ApiResponse.success("Status updated.",
                orderService.updateStatus(id, status, deliveryPartnerId)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Order deleted."));
    }
}
