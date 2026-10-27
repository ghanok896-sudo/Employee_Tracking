
package com.employee.tracking.dto;

public record LeaveReportDto(
        long totalLeaveRequests,
        long pendingRequests,
        long approvedRequests,
        long rejectedRequests,
        long cancelledRequests
) {
}
