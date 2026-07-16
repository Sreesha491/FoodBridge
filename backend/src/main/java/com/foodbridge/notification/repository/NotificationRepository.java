package com.foodbridge.notification.repository;

import com.foodbridge.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Notification} entities.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    /** All notifications for a user (newest first handled in service). */
    List<Notification> findByUserId(String userId);

    /** Unread notifications for a user. */
    List<Notification> findByUserIdAndReadFalse(String userId);

    /** Read notifications for a user. */
    List<Notification> findByUserIdAndReadTrue(String userId);

    /** Count unread notifications for a user. */
    long countByUserIdAndReadFalse(String userId);

    /** Delete all notifications for a user (bulk cleanup). */
    void deleteByUserId(String userId);
}
