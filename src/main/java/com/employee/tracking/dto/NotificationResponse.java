
package com.employee.tracking.dto;

import com.employee.tracking.entity.NotificationType;
import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        NotificationType type,
        String title,
        String message,
        boolean read,
        LocalDateTime createdAt
) {
}
