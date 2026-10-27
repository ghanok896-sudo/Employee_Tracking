
package com.employee.tracking.repository;

import com.employee.tracking.entity.Notification;
import com.employee.tracking.entity.NotificationType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUser_IdAndReadFalseOrderByCreatedAtDesc(
            Long userId);

    long countByUser_IdAndReadFalse(Long userId);

    boolean existsByUser_IdAndTypeAndMessage(
            Long userId,
            NotificationType type,
            String message);
}
