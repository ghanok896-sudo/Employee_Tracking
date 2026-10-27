
package com.employee.tracking.service;

import com.employee.tracking.dto.PerformanceReportDto;
import com.employee.tracking.entity.PerformanceRating;
import com.employee.tracking.repository.PerformanceReviewRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerformanceReportService {

    private final PerformanceReviewRepository repository;

    public PerformanceReportService(
            PerformanceReviewRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PerformanceReportDto getPerformanceReport() {

        return new PerformanceReportDto(
                repository.count(),
                repository.countByRating(PerformanceRating.POOR),
                repository.countByRating(
                        PerformanceRating.NEEDS_IMPROVEMENT),
                repository.countByRating(
                        PerformanceRating.MEETS_EXPECTATIONS),
                repository.countByRating(
                        PerformanceRating.EXCEEDS_EXPECTATIONS),
                repository.countByRating(
                        PerformanceRating.OUTSTANDING)
        );
    }
}

