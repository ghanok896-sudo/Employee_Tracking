
package com.employee.tracking.service;

import com.employee.tracking.dto.TaskReportDto;
import com.employee.tracking.entity.TaskStatus;
import com.employee.tracking.repository.TaskRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskReportService {

    private final TaskRepository taskRepository;

    public TaskReportService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public TaskReportDto getTaskReport() {

        long totalTasks = taskRepository.count();
        long pendingTasks =
                taskRepository.countByStatus(TaskStatus.TODO);
        long inProgressTasks =
                taskRepository.countByStatus(TaskStatus.IN_PROGRESS);
        long completedTasks =
                taskRepository.countByStatus(TaskStatus.COMPLETED);
        long cancelledTasks =
                taskRepository.countByStatus(TaskStatus.CANCELLED);

        double completionPercentage = totalTasks == 0
                ? 0.0
                : Math.round(
                        (completedTasks * 10000.0) / totalTasks
                ) / 100.0;

        return new TaskReportDto(
                totalTasks,
                pendingTasks,
                inProgressTasks,
                completedTasks,
                cancelledTasks,
                completionPercentage
        );
    }
}
