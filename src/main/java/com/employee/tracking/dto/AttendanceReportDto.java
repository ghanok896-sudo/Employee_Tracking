
package com.employee.tracking.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceReportDto(
        Long attendanceId,
        Long employeeId,
        String employeeName,
        String department,
        LocalDate attendanceDate,
        LocalDateTime checkIn,
        LocalDateTime checkOut,
        Long workingMinutes,
        String notes
) {
}
