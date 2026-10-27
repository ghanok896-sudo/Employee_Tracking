
package com.employee.tracking.controller;

import com.employee.tracking.dto.PayrollRequest;
import com.employee.tracking.dto.PayrollStatusRequest;
import com.employee.tracking.entity.Payroll;
import com.employee.tracking.entity.PayrollStatus;
import com.employee.tracking.service.PayrollService;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Payroll createPayroll(
            @Valid @RequestBody PayrollRequest request) {
        return payrollService.createPayroll(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Payroll> getAllPayrolls() {
        return payrollService.getAllPayrolls();
    }

    @GetMapping("/{payrollId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Payroll getPayrollById(
            @PathVariable Long payrollId) {
        return payrollService.getPayrollById(payrollId);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Payroll> getPayrollsByEmployee(
            @PathVariable Long employeeId) {
        return payrollService.getPayrollsByEmployee(employeeId);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Payroll> getPayrollsByStatus(
            @PathVariable PayrollStatus status) {
        return payrollService.getPayrollsByStatus(status);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Payroll> getPayrollsBetweenMonths(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startMonth,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endMonth) {
        return payrollService.getPayrollsBetweenMonths(
                startMonth, endMonth);
    }

    @PatchMapping("/{payrollId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Payroll updatePayrollStatus(
            @PathVariable Long payrollId,
            @Valid @RequestBody PayrollStatusRequest request) {
        return payrollService.updatePayrollStatus(
                payrollId, request.status());
    }
}
