
package com.employee.tracking.repository;

import com.employee.tracking.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PerformanceReviewRepository
        extends JpaRepository<PerformanceReview, Long> {

    List<PerformanceReview> findAllByOrderByReviewDateDesc();

    List<PerformanceReview>
    findByEmployee_IdOrderByReviewDateDesc(Long employeeId);

    List<PerformanceReview>
    findByReviewer_IdOrderByReviewDateDesc(Long reviewerId);

    List<PerformanceReview>
    findByEmployee_IdAndReviewDateBetweenOrderByReviewDateDesc(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate);

    boolean existsByEmployee_IdAndReviewDate(
            Long employeeId,
            LocalDate reviewDate);

long count();

long countByRating(
        com.employee.tracking.entity.PerformanceRating rating
);

}
