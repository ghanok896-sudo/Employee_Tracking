
package com.employee.tracking.controller;

import com.employee.tracking.dto.PerformanceReportDto;
import com.employee.tracking.service.PerformanceReportService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports/performance")
public class PerformanceReportController {

    private final PerformanceReportService service;

    public PerformanceReportController(
            PerformanceReportService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public PerformanceReportDto getPerformanceReport() {
        return service.getPerformanceReport();
    }
}
