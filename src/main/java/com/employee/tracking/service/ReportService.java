
package com.employee.tracking.service;

import com.employee.tracking.dto.AttendanceReportDto;
import com.employee.tracking.entity.Attendance;
import com.employee.tracking.repository.AttendanceRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final AttendanceRepository attendanceRepository;

    public ReportService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Transactional(readOnly = true)
    public List<AttendanceReportDto> getAttendanceReport(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Start date and end date are required.");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date.");
        }

        return attendanceRepository
                .findByAttendanceDateBetweenOrderByAttendanceDateDesc(
                        startDate, endDate)
                .stream()
                .map(this::toAttendanceReportDto)
                .toList();
    }

    private AttendanceReportDto toAttendanceReportDto(
            Attendance attendance) {

        var employee = attendance.getEmployee();

        return new AttendanceReportDto(
                attendance.getId(),
                employee.getId(),
                employee.getFullName(),
                employee.getDepartment(),
                attendance.getAttendanceDate(),
                attendance.getCheckIn(),
                attendance.getCheckOut(),
                attendance.getWorkingMinutes(),
                attendance.getNotes()
        );
    }
}
