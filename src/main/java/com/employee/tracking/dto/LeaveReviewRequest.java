
package com.employee.tracking.dto;

import com.employee.tracking.entity.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LeaveReviewRequest(
        @NotNull(message = "Review decision is required")
        LeaveStatus decision,

        @NotNull(message = "Reviewer user ID is required")
        Long reviewerUserId,

        @Size(max = 1000, message = "Review comments cannot exceed 1000 characters")
        String reviewComments
) {}
