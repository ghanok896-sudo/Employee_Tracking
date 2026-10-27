
package com.employee.tracking.controller;

import com.employee.tracking.dto.NotificationResponse;
import com.employee.tracking.entity.AppUser;
import com.employee.tracking.repository.AppUserRepository;
import com.employee.tracking.service.NotificationService;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private final NotificationService notificationService;
    private final AppUserRepository appUserRepository;

    public NotificationController(
            NotificationService notificationService,
            AppUserRepository appUserRepository) {
        this.notificationService = notificationService;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping
    public List<NotificationResponse> getNotifications(
            Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        return notificationService.getNotifications(userId);
    }

    @GetMapping("/unread")
    public List<NotificationResponse> getUnreadNotifications(
            Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        return notificationService.getUnreadNotifications(userId);
    }

    @GetMapping("/unread/count")
    public long getUnreadCount(Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        return notificationService.getUnreadCount(userId);
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationResponse markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);

        return notificationService.markAsRead(userId, notificationId);
    }

    private Long getAuthenticatedUserId(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication is required.");
        }

        String email = authentication.getName();

        AppUser user = appUserRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user was not found."));

        return user.getId();
    }
}
