package com.foodbridge.notification.controller;

import com.foodbridge.common.response.ApiResponse;
import com.foodbridge.notification.dto.NotificationRequest;
import com.foodbridge.notification.dto.NotificationResponse;
import com.foodbridge.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for notification management.
 * Base path: /v1/notifications
 */
@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Manage in-app notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all notifications (ADMIN only)")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAllNotifications() {
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved.", notificationService.getAllNotifications()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID")
    public ResponseEntity<ApiResponse<NotificationResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Notification retrieved.", notificationService.getNotificationById(id)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all notifications for a user")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved.", notificationService.getNotificationsByUser(userId)));
    }

    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "Get unread notifications for a user")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnread(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success("Unread notifications retrieved.", notificationService.getUnreadByUser(userId)));
    }

    @GetMapping("/user/{userId}/unread-count")
    @Operation(summary = "Get count of unread notifications for a user")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(@PathVariable String userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success("Unread count retrieved.", Map.of("unreadCount", count)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a notification (ADMIN only)")
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notification created.", notificationService.createNotification(request)));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Marked as read.", notificationService.markAsRead(id)));
    }

    @PatchMapping("/user/{userId}/read-all")
    @Operation(summary = "Mark all notifications as read for a user")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> markAllAsRead(@PathVariable String userId) {
        int count = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read.",
                Map.of("markedCount", count)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.success("Notification deleted."));
    }

    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Delete all notifications for a user")
    public ResponseEntity<ApiResponse<Void>> deleteAllForUser(@PathVariable String userId) {
        notificationService.deleteAllForUser(userId);
        return ResponseEntity.ok(ApiResponse.success("All notifications deleted."));
    }
}
