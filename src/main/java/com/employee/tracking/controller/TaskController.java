
package com.employee.tracking.controller;

import com.employee.tracking.dto.TaskRequest;
import com.employee.tracking.dto.TaskStatusRequest;
import com.employee.tracking.entity.Task;
import com.employee.tracking.entity.TaskStatus;
import com.employee.tracking.service.TaskService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // ADMIN and MANAGER can create tasks.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Task createTask(@Valid @RequestBody TaskRequest request) {
        return taskService.createTask(request);
    }

    // ADMIN and MANAGER can view all tasks.
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    // ADMIN and MANAGER can view a task by ID.
    @GetMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Task getTaskById(@PathVariable Long taskId) {
        return taskService.getTaskById(taskId);
    }

    // ADMIN and MANAGER can view an employee's tasks.
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Task> getTasksByEmployee(
            @PathVariable Long employeeId) {
        return taskService.getTasksByEmployee(employeeId);
    }

    // ADMIN and MANAGER can filter tasks by status.
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Task> getTasksByStatus(
            @PathVariable TaskStatus status) {
        return taskService.getTasksByStatus(status);
    }

    // Only ADMIN and MANAGER can update task status in this version.
    @PatchMapping("/{taskId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Task updateTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskStatusRequest request) {
        return taskService.updateTaskStatus(taskId, request);
    }
}
