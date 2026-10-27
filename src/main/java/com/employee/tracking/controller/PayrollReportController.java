
package com.employee.tracking.controller;

import com.employee.tracking.dto.PayrollReportDto;
import com.employee.tracking.service.PayrollReportService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports/payroll")
public class PayrollReportController {

    private final PayrollReportService payrollReportService;

    public PayrollReportController(
            PayrollReportService payrollReportService) {
        this.payrollReportService = payrollReportService;
    }

    @GetMapping("/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PayrollReportDto> getMonthlyPayrollReport(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate month) {

        return payrollReportService.getMonthlyPayrollReport(month);
    }
}
