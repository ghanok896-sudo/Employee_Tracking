
package com.employee.tracking.service;

import com.employee.tracking.dto.LeaveReportDto;
import com.employee.tracking.entity.LeaveStatus;
import com.employee.tracking.repository.LeaveRequestRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeaveReportService {

    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveReportService(
            LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Transactional(readOnly = true)
    public LeaveReportDto getLeaveReport() {

        long total = leaveRequestRepository.count();

        long pending =
                leaveRequestRepository.countByStatus(LeaveStatus.PENDING);

        long approved =
                leaveRequestRepository.countByStatus(LeaveStatus.APPROVED);

        long rejected =
                leaveRequestRepository.countByStatus(LeaveStatus.REJECTED);

        long cancelled =
                leaveRequestRepository.countByStatus(LeaveStatus.CANCELLED);

        return new LeaveReportDto(
                total,
                pending,
                approved,
                rejected,
                cancelled
        );
    }
}
