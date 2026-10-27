
package com.employee.tracking.service;

import com.employee.tracking.entity.NotificationType;
import com.employee.tracking.entity.Task;
import com.employee.tracking.entity.TaskStatus;
import com.employee.tracking.repository.TaskRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskDeadlineNotificationService {

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    public TaskDeadlineNotificationService(
            TaskRepository taskRepository,
            NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
    }

    // Check every day at 9:00 AM using the server's local time.
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void notifyUpcomingDeadlines() {

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<Task> tasks = taskRepository.findAll();

        for (Task task : tasks) {

            if (task.getDueDate() == null) {
                continue;
            }

            if (task.getStatus() == TaskStatus.COMPLETED
                    || task.getStatus() == TaskStatus.CANCELLED) {
                continue;
            }

            if (task.getDueDate().equals(today)
                    || task.getDueDate().equals(tomorrow)) {

                // Employee ID and application user ID may differ.
                // Use the employee's email to locate the account.
                String employeeEmail =
                        task.getAssignedEmployee().getEmail();

                if (employeeEmail == null || employeeEmail.isBlank()) {
                    continue;
                }

                // The email lookup is handled by NotificationService's
                // existing account mapping helper in the next step.
                notificationService.notifyDeadlineByEmployeeEmail(
                        employeeEmail,
                        task.getTitle(),
                        task.getDueDate()
                );
            }
        }
    }
}
