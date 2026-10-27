
package com.employee.tracking.controller;

import com.employee.tracking.dto.AttendanceRequest;
import com.employee.tracking.entity.Attendance;
import com.employee.tracking.service.AttendanceService;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /*
     * Temporary development endpoints.
     * Employee ID is supplied explicitly for now.
     * We will connect it to the authenticated user in the next step.
     */

    @PostMapping("/check-in/{employeeId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public Attendance checkIn(
            @PathVariable Long employeeId,
            @Valid @RequestBody(required = false) AttendanceRequest request) {
        return attendanceService.checkIn(employeeId, request);
    }

    @PostMapping("/check-out/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public Attendance checkOut(@PathVariable Long employeeId) {
        return attendanceService.checkOut(employeeId);
    }

    @GetMapping("/my-history/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public List<Attendance> getMyHistory(
            @PathVariable Long employeeId) {
        return attendanceService.getMyHistory(employeeId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Attendance> getAttendance(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        if (date != null) {
            return attendanceService.getAttendanceByDate(date);
        }

        return attendanceService.getAllAttendance();
    }
}
