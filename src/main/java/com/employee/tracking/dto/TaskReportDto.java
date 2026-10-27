
package com.employee.tracking.dto;

public record TaskReportDto(
        long totalTasks,
        long pendingTasks,
        long inProgressTasks,
        long completedTasks,
        long cancelledTasks,
        double completionPercentage
) {
}
