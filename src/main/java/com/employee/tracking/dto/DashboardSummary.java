
package com.employee.tracking.dto;

import java.math.BigDecimal;

public record DashboardSummary(
        long totalEmployees,
        long activeEmployees,
        long inactiveEmployees,
        long totalTasks,
        long pendingTasks,
        long inProgressTasks,
        long completedTasks,
        long pendingLeaveRequests,
        long approvedLeaveRequests,
        long rejectedLeaveRequests,
        long totalPayrollRecords,
        BigDecimal totalNetPayroll,
        long totalPerformanceReviews
) {}
