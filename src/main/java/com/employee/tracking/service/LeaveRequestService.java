
package com.employee.tracking.service;

import com.employee.tracking.entity.AppUser;
import com.employee.tracking.entity.Employee;
import com.employee.tracking.entity.LeaveRequest;
import com.employee.tracking.entity.LeaveStatus;
import com.employee.tracking.entity.LeaveType;
import com.employee.tracking.entity.NotificationType;
import com.employee.tracking.repository.AppUserRepository;
import com.employee.tracking.repository.EmployeeRepository;
import com.employee.tracking.repository.LeaveRequestRepository;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final AppUserRepository appUserRepository;
    private final NotificationService notificationService;

    public LeaveRequestService(
            LeaveRequestRepository leaveRequestRepository,
            EmployeeRepository employeeRepository,
            AppUserRepository appUserRepository,
            NotificationService notificationService) {

        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.appUserRepository = appUserRepository;
        this.notificationService = notificationService;
    }

    public LeaveRequest createLeaveRequest(
            Long employeeId,
            LeaveType leaveType,
            LocalDate startDate,
            LocalDate endDate,
            String reason) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found"));

        if (!employee.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Inactive employees cannot request leave");
        }

        validateDates(startDate, endDate);

        if (leaveType == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Leave type is required");
        }

        if (reason == null || reason.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Leave reason is required");
        }

        if (reason.trim().length() > 1000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Leave reason cannot exceed 1000 characters");
        }

        boolean overlapping = leaveRequestRepository
                .existsByEmployee_IdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        employeeId, LeaveStatus.PENDING, endDate, startDate)
                || leaveRequestRepository
                .existsByEmployee_IdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        employeeId, LeaveStatus.APPROVED, endDate, startDate);

        if (overlapping) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An overlapping pending or approved leave request already exists");
        }

        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setLeaveType(leaveType);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setReason(reason.trim());
        request.setStatus(LeaveStatus.PENDING);

        return leaveRequestRepository.save(request);
    }

    public List<LeaveRequest> getAllLeaveRequests() {
        List<LeaveRequest> requests =
                leaveRequestRepository.findAllByOrderByCreatedAtDesc();

        initializeEmployees(requests);
        return requests;
    }

    public LeaveRequest getLeaveRequestById(Long requestId) {
        LeaveRequest request = findRequest(requestId);
        initializeRequest(request);
        return request;
    }

    public List<LeaveRequest> getEmployeeLeaveRequests(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Employee not found");
        }

        List<LeaveRequest> requests = leaveRequestRepository
                .findByEmployee_IdOrderByCreatedAtDesc(employeeId);

        initializeEmployees(requests);
        return requests;
    }

    public List<LeaveRequest> getLeaveRequestsByStatus(LeaveStatus status) {
        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Leave status is required");
        }

        List<LeaveRequest> requests =
                leaveRequestRepository.findByStatusOrderByCreatedAtDesc(status);

        initializeEmployees(requests);
        return requests;
    }

    public LeaveRequest reviewLeaveRequest(
            Long requestId,
            Long reviewerUserId,
            LeaveStatus decision,
            String reviewComments) {

        LeaveRequest request = findRequest(requestId);

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only pending leave requests can be reviewed");
        }

        if (decision != LeaveStatus.APPROVED
                && decision != LeaveStatus.REJECTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Review decision must be APPROVED or REJECTED");
        }

        if (reviewComments != null && reviewComments.length() > 1000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Review comments cannot exceed 1000 characters");
        }

        AppUser reviewer = appUserRepository.findById(reviewerUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reviewer account not found"));

        request.setStatus(decision);
        request.setReviewedBy(reviewer);
        request.setReviewComments(
                reviewComments == null || reviewComments.isBlank()
                        ? null : reviewComments.trim());

        LeaveRequest saved = leaveRequestRepository.save(request);

        // Notify the employee's application account, if one matches.
        String employeeEmail = saved.getEmployee().getEmail();

        if (employeeEmail != null && !employeeEmail.isBlank()) {
            appUserRepository.findByEmailIgnoreCase(employeeEmail)
                    .ifPresent(user -> {
                        NotificationType type =
                                decision == LeaveStatus.APPROVED
                                        ? NotificationType.LEAVE_APPROVED
                                        : NotificationType.LEAVE_REJECTED;

                        String title =
                                decision == LeaveStatus.APPROVED
                                        ? "Leave Request Approved"
                                        : "Leave Request Rejected";

                        String message = "Your "
                                + saved.getLeaveType()
                                + " leave request from "
                                + saved.getStartDate()
                                + " to "
                                + saved.getEndDate()
                                + " was "
                                + decision.name().toLowerCase()
                                + ".";

                        notificationService.createNotification(
                                user.getId(),
                                type,
                                title,
                                message
                        );
                    });
        }

        initializeRequest(saved);
        return saved;
    }

    public LeaveRequest cancelLeaveRequest(
            Long requestId,
            Long employeeId) {

        LeaveRequest request = findRequest(requestId);

        if (!request.getEmployee().getId().equals(employeeId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can cancel only your own leave request");
        }

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only pending leave requests can be cancelled");
        }

        request.setStatus(LeaveStatus.CANCELLED);

        LeaveRequest saved = leaveRequestRepository.save(request);
        initializeRequest(saved);
        return saved;
    }

    private void validateDates(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start date and end date are required");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Leave start date cannot be in the past");
        }

        if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End date cannot be before start date");
        }
    }

    private LeaveRequest findRequest(Long requestId) {
        return leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Leave request not found"));
    }

    private void initializeEmployees(List<LeaveRequest> requests) {
        requests.forEach(this::initializeRequest);
    }

    private void initializeRequest(LeaveRequest request) {
        request.getEmployee().getFullName();

        if (request.getReviewedBy() != null) {
            request.getReviewedBy().getFullName();
        }
    }
}
