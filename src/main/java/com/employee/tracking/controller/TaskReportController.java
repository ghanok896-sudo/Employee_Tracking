
package com.employee.tracking.controller;

import com.employee.tracking.dto.TaskReportDto;
import com.employee.tracking.service.TaskReportService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports/tasks")
public class TaskReportController {

    private final TaskReportService taskReportService;

    public TaskReportController(TaskReportService taskReportService) {
        this.taskReportService = taskReportService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public TaskReportDto getTaskReport() {
        return taskReportService.getTaskReport();
    }
}
