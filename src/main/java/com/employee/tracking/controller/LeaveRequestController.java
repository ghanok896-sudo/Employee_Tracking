
package com.employee.tracking.controller;

import com.employee.tracking.dto.LeaveRequestDto;
import com.employee.tracking.dto.LeaveReviewRequest;
import com.employee.tracking.entity.LeaveRequest;
import com.employee.tracking.entity.LeaveStatus;
import com.employee.tracking.service.LeaveRequestService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @PostMapping("/employee/{employeeId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public LeaveRequest createLeaveRequest(
            @PathVariable Long employeeId,
            @Valid @RequestBody LeaveRequestDto request) {

        return leaveRequestService.createLeaveRequest(
                employeeId,
                request.leaveType(),
                request.startDate(),
                request.endDate(),
                request.reason());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestService.getAllLeaveRequests();
    }

    @GetMapping("/{requestId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public LeaveRequest getLeaveRequestById(
            @PathVariable Long requestId) {
        return leaveRequestService.getLeaveRequestById(requestId);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public List<LeaveRequest> getEmployeeLeaveRequests(
            @PathVariable Long employeeId) {
        return leaveRequestService.getEmployeeLeaveRequests(employeeId);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<LeaveRequest> getLeaveRequestsByStatus(
            @PathVariable LeaveStatus status) {
        return leaveRequestService.getLeaveRequestsByStatus(status);
    }

    @PatchMapping("/{requestId}/review")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public LeaveRequest reviewLeaveRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody LeaveReviewRequest request) {

        return leaveRequestService.reviewLeaveRequest(
                requestId,
                request.reviewerUserId(),
                request.decision(),
                request.reviewComments());
    }

    @PatchMapping("/{requestId}/cancel/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public LeaveRequest cancelLeaveRequest(
            @PathVariable Long requestId,
            @PathVariable Long employeeId) {
        return leaveRequestService.cancelLeaveRequest(requestId, employeeId);
    }
}
