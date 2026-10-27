
package com.employee.tracking.dto;

import com.employee.tracking.entity.PerformanceRating;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PerformanceReviewRequest(

        @NotNull(message = "Employee ID is required")
        Long employeeId,

        @NotNull(message = "Performance rating is required")
        PerformanceRating rating,

        @NotNull(message = "Score is required")
        @Min(value = 1, message = "Score must be at least 1")
        @Max(value = 5, message = "Score cannot exceed 5")
        Integer score,

        @NotNull(message = "Review date is required")
        LocalDate reviewDate,

        @Size(max = 2000, message = "Feedback cannot exceed 2000 characters")
        String feedback,

        @Size(max = 2000, message = "Goals cannot exceed 2000 characters")
        String goals
) {}
