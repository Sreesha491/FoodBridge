package com.foodbridge.notification.dto;

import com.foodbridge.notification.model.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Response DTO for a notification.
 */
@Data
@Builder
public class NotificationResponse {

    private String id;
    private String userId;
    private String title;
    private String message;
    private NotificationType type;
    private boolean read;
    private String referenceId;
    private String referenceType;
    private Instant createdAt;
}
