
package com.employee.tracking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PayrollRequest(

        @NotNull(message = "Employee ID is required")
        Long employeeId,

        @NotNull(message = "Payroll month is required")
        LocalDate payrollMonth,

        @NotNull(message = "Basic salary is required")
        @DecimalMin(value = "0.00", message = "Basic salary cannot be negative")
        @Digits(integer = 10, fraction = 2,
                message = "Basic salary supports up to 2 decimal places")
        BigDecimal basicSalary,

        @NotNull(message = "Allowances are required")
        @DecimalMin(value = "0.00", message = "Allowances cannot be negative")
        @Digits(integer = 10, fraction = 2,
                message = "Allowances support up to 2 decimal places")
        BigDecimal allowances,

        @NotNull(message = "Deductions are required")
        @DecimalMin(value = "0.00", message = "Deductions cannot be negative")
        @Digits(integer = 10, fraction = 2,
                message = "Deductions support up to 2 decimal places")
        BigDecimal deductions
) {}
