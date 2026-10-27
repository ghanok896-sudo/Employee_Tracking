
package com.employee.tracking.dto;

import com.employee.tracking.entity.PayrollStatus;
import jakarta.validation.constraints.NotNull;

public record PayrollStatusRequest(

        @NotNull(message = "Payroll status is required")
        PayrollStatus status

) {}
