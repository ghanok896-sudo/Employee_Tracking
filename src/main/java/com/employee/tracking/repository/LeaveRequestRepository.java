
package com.employee.tracking.repository;

import com.employee.tracking.entity.LeaveRequest;
import com.employee.tracking.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository
        extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findAllByOrderByCreatedAtDesc();

    List<LeaveRequest> findByEmployee_IdOrderByCreatedAtDesc(
            Long employeeId);

    List<LeaveRequest> findByStatusOrderByCreatedAtDesc(
            LeaveStatus status);

    List<LeaveRequest> findByEmployee_IdAndStatusOrderByCreatedAtDesc(
            Long employeeId,
            LeaveStatus status);

    List<LeaveRequest>
    findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate endDate,
            LocalDate startDate);

    boolean existsByEmployee_IdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long employeeId,
            LeaveStatus status,
            LocalDate requestedEndDate,
            LocalDate requestedStartDate);
    
    

long countByStatus(
        com.employee.tracking.entity.LeaveStatus status);

}
