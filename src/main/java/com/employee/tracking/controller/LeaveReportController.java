
package com.employee.tracking.controller;

import com.employee.tracking.dto.LeaveReportDto;
import com.employee.tracking.service.LeaveReportService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports/leaves")
public class LeaveReportController {

    private final LeaveReportService leaveReportService;

    public LeaveReportController(LeaveReportService leaveReportService) {
        this.leaveReportService = leaveReportService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public LeaveReportDto getLeaveReport() {
        return leaveReportService.getLeaveReport();
    }
}
