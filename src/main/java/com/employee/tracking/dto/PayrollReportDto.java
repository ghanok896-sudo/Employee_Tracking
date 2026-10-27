
package com.employee.tracking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PayrollReportDto(
        Long payrollId,
        Long employeeId,
        String employeeName,
        LocalDate payrollMonth,
        BigDecimal basicSalary,
        BigDecimal allowances,
        BigDecimal deductions,
        BigDecimal netSalary,
        String status
) {
}
