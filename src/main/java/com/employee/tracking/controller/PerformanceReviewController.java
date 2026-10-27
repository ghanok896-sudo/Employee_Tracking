
package com.employee.tracking.controller;

import com.employee.tracking.dto.PerformanceReviewRequest;
import com.employee.tracking.entity.PerformanceReview;
import com.employee.tracking.repository.AppUserRepository;
import com.employee.tracking.service.PerformanceReviewService;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/performance-reviews")
public class PerformanceReviewController {

    private final PerformanceReviewService reviewService;
    private final AppUserRepository appUserRepository;

    public PerformanceReviewController(
            PerformanceReviewService reviewService,
            AppUserRepository appUserRepository) {
        this.reviewService = reviewService;
        this.appUserRepository = appUserRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public PerformanceReview createReview(
            @Valid @RequestBody PerformanceReviewRequest request,
            Authentication authentication) {

        if (authentication == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        String email = authentication.getName();

        Long reviewerUserId = appUserRepository
                .findByEmailIgnoreCase(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user account not found"));

        return reviewService.createReview(request, reviewerUserId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<PerformanceReview> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public PerformanceReview getReviewById(
            @PathVariable Long reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<PerformanceReview> getReviewsByEmployee(
            @PathVariable Long employeeId) {
        return reviewService.getReviewsByEmployee(employeeId);
    }

    @GetMapping("/reviewer/{reviewerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<PerformanceReview> getReviewsByReviewer(
            @PathVariable Long reviewerId) {
        return reviewService.getReviewsByReviewer(reviewerId);
    }

    @GetMapping("/employee/{employeeId}/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<PerformanceReview> getReviewsBetweenDates(
            @PathVariable Long employeeId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return reviewService.getReviewsBetweenDates(
                employeeId, startDate, endDate);
    }
}
