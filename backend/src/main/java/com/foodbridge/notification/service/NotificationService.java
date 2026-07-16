package com.foodbridge.notification.service;

import com.foodbridge.common.exception.ResourceNotFoundException;
import com.foodbridge.notification.dto.NotificationRequest;
import com.foodbridge.notification.dto.NotificationResponse;
import com.foodbridge.notification.model.Notification;
import com.foodbridge.notification.repository.NotificationRepository;
import com.foodbridge.user.model.User;
import com.foodbridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Business logic layer for notification management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // ─── Create ───────────────────────────────────────────────────────────

    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
        Notification notification = Notification.builder()
                .user(user)
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .referenceId(request.getReferenceId())
                .referenceType(request.getReferenceType())
                .read(false)
                .createdAt(Instant.now())
                .build();
        return toResponse(notificationRepository.save(notification));
    }

    // ─── Read ─────────────────────────────────────────────────────────────

    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll().stream().map(this::toResponse).toList();
    }

    public NotificationResponse getNotificationById(String id) {
        return toResponse(findById(id));
    }

    public List<NotificationResponse> getNotificationsByUser(String userId) {
        return notificationRepository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    public List<NotificationResponse> getUnreadByUser(String userId) {
        return notificationRepository.findByUserIdAndReadFalse(userId)
                .stream().map(this::toResponse).toList();
    }

    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    // ─── Update ───────────────────────────────────────────────────────────

    public NotificationResponse markAsRead(String id) {
        Notification notification = findById(id);
        notification.setRead(true);
        return toResponse(notificationRepository.save(notification));
    }

    public int markAllAsRead(String userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalse(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
        return unread.size();
    }

    // ─── Delete ───────────────────────────────────────────────────────────

    public void deleteNotification(String id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notification", "id", id);
        }
        notificationRepository.deleteById(id);
    }

    public void deleteAllForUser(String userId) {
        notificationRepository.deleteByUserId(userId);
        log.info("Deleted all notifications for user [{}]", userId);
    }

    // ─── Private Helpers ──────────────────────────────────────────────────

    private Notification findById(String id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
    }

    public NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .userId(n.getUser() != null ? n.getUser().getId() : null)
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .read(n.isRead())
                .referenceId(n.getReferenceId())
                .referenceType(n.getReferenceType())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
