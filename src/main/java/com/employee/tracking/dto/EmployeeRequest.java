
package com.employee.tracking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmployeeRequest(

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    String fullName,

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    String email,

    @Size(max = 30, message = "Phone number cannot exceed 30 characters")
    String phone,

    @Size(max = 100, message = "Department cannot exceed 100 characters")
    String department,

    @Size(max = 100, message = "Designation cannot exceed 100 characters")
    String designation,

    LocalDate joiningDate,

    Boolean active
) {
}
