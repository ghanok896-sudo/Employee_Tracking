
package com.employee.tracking.dto;

import jakarta.validation.constraints.Size;

public record AttendanceRequest(

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    String notes

) {
}

