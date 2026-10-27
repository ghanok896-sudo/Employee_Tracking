
package com.employee.tracking.dto;

public record PerformanceReportDto(
        long totalReviews,
        long poorReviews,
        long needsImprovementReviews,
        long meetsExpectationsReviews,
        long exceedsExpectationsReviews,
        long outstandingReviews
) {
}
