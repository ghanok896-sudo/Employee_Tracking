
package com.employee.tracking.service;

import com.employee.tracking.dto.TaskRequest;
import com.employee.tracking.dto.TaskStatusRequest;
import com.employee.tracking.entity.AppUser;
import com.employee.tracking.entity.Employee;
import com.employee.tracking.entity.NotificationType;
import com.employee.tracking.entity.Task;
import com.employee.tracking.entity.TaskStatus;
import com.employee.tracking.repository.AppUserRepository;
import com.employee.tracking.repository.EmployeeRepository;
import com.employee.tracking.repository.TaskRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final AppUserRepository appUserRepository;
    private final NotificationService notificationService;

    public TaskService(
            TaskRepository taskRepository,
            EmployeeRepository employeeRepository,
            AppUserRepository appUserRepository,
            NotificationService notificationService) {

        this.taskRepository = taskRepository;
        this.employeeRepository = employeeRepository;
        this.appUserRepository = appUserRepository;
        this.notificationService = notificationService;
    }

    public Task createTask(TaskRequest request) {

        Employee employee = employeeRepository
                .findById(request.assignedEmployeeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Assigned employee was not found"));

        if (!employee.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot assign a task to an inactive employee");
        }

        Task task = new Task();
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setAssignedEmployee(employee);
        task.setDueDate(request.dueDate());
        task.setStatus(TaskStatus.TODO);

        Task savedTask = taskRepository.save(task);

        // Find the application account associated with this employee.
        appUserRepository.findByEmailIgnoreCase(employee.getEmail())
                .ifPresent(user -> notificationService.createNotification(
                        user.getId(),
                        NotificationType.TASK_ASSIGNED,
                        "New Task Assigned",
                        "You have been assigned a new task: "
                                + savedTask.getTitle()
                ));

        return savedTask;
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {

        List<Task> tasks =
                taskRepository.findAllByOrderByCreatedAtDesc();

        initializeEmployees(tasks);
        return tasks;
    }

    @Transactional(readOnly = true)
    public Task getTaskById(Long taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task was not found"));

        task.getAssignedEmployee().getFullName();
        return task;
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByEmployee(Long employeeId) {

        List<Task> tasks = taskRepository
                .findByAssignedEmployee_IdOrderByCreatedAtDesc(employeeId);

        initializeEmployees(tasks);
        return tasks;
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByStatus(TaskStatus status) {

        List<Task> tasks =
                taskRepository.findByStatusOrderByCreatedAtDesc(status);

        initializeEmployees(tasks);
        return tasks;
    }

    public Task updateTaskStatus(
            Long taskId,
            TaskStatusRequest request) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task was not found"));

        TaskStatus currentStatus = task.getStatus();
        TaskStatus newStatus = request.status();

        if (currentStatus == TaskStatus.COMPLETED
                || currentStatus == TaskStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A completed or cancelled task cannot be updated");
        }

        if (newStatus == TaskStatus.TODO
                || newStatus == TaskStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This endpoint supports IN_PROGRESS or COMPLETED only");
        }

        task.setStatus(newStatus);

        Task savedTask = taskRepository.save(task);
        savedTask.getAssignedEmployee().getFullName();

        return savedTask;
    }

    private void initializeEmployees(List<Task> tasks) {
        tasks.forEach(task ->
                task.getAssignedEmployee().getFullName());
    }
}
