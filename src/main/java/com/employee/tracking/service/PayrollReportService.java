
package com.employee.tracking.service;

import com.employee.tracking.dto.PayrollReportDto;
import com.employee.tracking.entity.Payroll;
import com.employee.tracking.repository.PayrollRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PayrollReportService {

    private final PayrollRepository payrollRepository;

    public PayrollReportService(PayrollRepository payrollRepository) {
        this.payrollRepository = payrollRepository;
    }

    @Transactional(readOnly = true)
    public List<PayrollReportDto> getMonthlyPayrollReport(
            LocalDate month) {

        if (month == null || month.getDayOfMonth() != 1) {
            throw new IllegalArgumentException(
                    "Payroll month must be the first day of a month.");
        }

        return payrollRepository
                .findByPayrollMonthOrderByEmployee_IdAsc(month)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private PayrollReportDto toDto(Payroll payroll) {
        var employee = payroll.getEmployee();

        return new PayrollReportDto(
                payroll.getId(),
                employee.getId(),
                employee.getFullName(),
                payroll.getPayrollMonth(),
                payroll.getBasicSalary(),
                payroll.getAllowances(),
                payroll.getDeductions(),
                payroll.getNetSalary(),
                payroll.getStatus().name()
        );
    }
}
