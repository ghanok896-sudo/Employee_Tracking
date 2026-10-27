
package com.employee.tracking.service;

import com.employee.tracking.dto.NotificationResponse;
import com.employee.tracking.entity.AppUser;
import com.employee.tracking.entity.Notification;
import com.employee.tracking.repository.AppUserRepository;
import com.employee.tracking.repository.NotificationRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.employee.tracking.entity.NotificationType;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AppUserRepository appUserRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            AppUserRepository appUserRepository) {
        this.notificationRepository = notificationRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId) {
        requireUser(userId);

        return notificationRepository
                .findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        requireUser(userId);

        return notificationRepository
                .findByUser_IdAndReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        requireUser(userId);
        return notificationRepository.countByUser_IdAndReadFalse(userId);
    }

    @Transactional
    public NotificationResponse markAsRead(
            Long userId, Long notificationId) {

        requireUser(userId);

        Notification notification = notificationRepository
                .findById(notificationId)
                .filter(item -> item.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notification not found for this user."
                ));

        notification.markAsRead();

        return toResponse(notificationRepository.save(notification));
    }

    private AppUser requireUser(Long userId) {
        return appUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found."
                ));
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
    
    @Transactional
    public void createNotification(
            Long userId,
            NotificationType type,
            String title,
            String message) {

        AppUser user = requireUser(userId);

        Notification notification = new Notification(
                user,
                type,
                title,
                message
        );

        notificationRepository.save(notification);
    }
    

public void notifyDeadlineByEmployeeEmail(
        String employeeEmail,
        String taskTitle,
        java.time.LocalDate dueDate) {

    if (employeeEmail == null || employeeEmail.isBlank()) {
        return;
    }

    AppUser user = appUserRepository
            .findByEmailIgnoreCase(employeeEmail)
            .orElse(null);

    if (user == null) {
        return;
    }

    String message = "Task \"" + taskTitle
            + "\" is due on " + dueDate + ".";

    boolean alreadyExists =
            notificationRepository.existsByUser_IdAndTypeAndMessage(
                    user.getId(),
                    NotificationType.TASK_DEADLINE,
                    message);

    if (!alreadyExists) {
        createNotification(
                user.getId(),
                NotificationType.TASK_DEADLINE,
                "Upcoming Task Deadline",
                message);
    }
}

    
    
}
