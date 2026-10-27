
package com.employee.tracking.dto;

import com.employee.tracking.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record TaskStatusRequest(

    @NotNull(message = "Task status is required")
    TaskStatus status

) {
}
