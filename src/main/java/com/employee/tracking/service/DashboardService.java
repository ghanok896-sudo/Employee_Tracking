
package com.employee.tracking.service;

import com.employee.tracking.dto.DashboardSummary;
import com.employee.tracking.entity.LeaveStatus;
import com.employee.tracking.entity.Payroll;
import com.employee.tracking.entity.PayrollStatus;
import com.employee.tracking.entity.TaskStatus;
import com.employee.tracking.repository.EmployeeRepository;
import com.employee.tracking.repository.LeaveRequestRepository;
import com.employee.tracking.repository.PayrollRepository;
import com.employee.tracking.repository.PerformanceReviewRepository;
import com.employee.tracking.repository.TaskRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PayrollRepository payrollRepository;
    private final PerformanceReviewRepository performanceReviewRepository;

    public DashboardService(
            EmployeeRepository employeeRepository,
            TaskRepository taskRepository,
            LeaveRequestRepository leaveRequestRepository,
            PayrollRepository payrollRepository,
            PerformanceReviewRepository performanceReviewRepository) {

        this.employeeRepository = employeeRepository;
        this.taskRepository = taskRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.payrollRepository = payrollRepository;
        this.performanceReviewRepository = performanceReviewRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummary getDashboardSummary() {

        long totalEmployees = employeeRepository.count();
        long activeEmployees = employeeRepository.countByActiveTrue();
        long inactiveEmployees = employeeRepository.countByActiveFalse();

        long totalTasks = taskRepository.count();
        long pendingTasks = taskRepository.countByStatus(TaskStatus.TODO);
        long inProgressTasks =
                taskRepository.countByStatus(TaskStatus.IN_PROGRESS);
        long completedTasks =
                taskRepository.countByStatus(TaskStatus.COMPLETED);

        long pendingLeaveRequests =
                leaveRequestRepository.countByStatus(LeaveStatus.PENDING);
        long approvedLeaveRequests =
                leaveRequestRepository.countByStatus(LeaveStatus.APPROVED);
        long rejectedLeaveRequests =
                leaveRequestRepository.countByStatus(LeaveStatus.REJECTED);

        long totalPayrollRecords = payrollRepository.count();

        BigDecimal totalNetPayroll = payrollRepository.findAll()
                .stream()
                .filter(payroll -> payroll.getStatus() == PayrollStatus.PAID)
                .map(Payroll::getNetSalary)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalPerformanceReviews =
                performanceReviewRepository.count();

        return new DashboardSummary(
                totalEmployees,
                activeEmployees,
                inactiveEmployees,
                totalTasks,
                pendingTasks,
                inProgressTasks,
                completedTasks,
                pendingLeaveRequests,
                approvedLeaveRequests,
                rejectedLeaveRequests,
                totalPayrollRecords,
                totalNetPayroll,
                totalPerformanceReviews
        );
    }
}
