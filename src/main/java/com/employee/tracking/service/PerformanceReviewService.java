
package com.employee.tracking.service;

import com.employee.tracking.dto.PerformanceReviewRequest;
import com.employee.tracking.entity.AppUser;
import com.employee.tracking.entity.Employee;
import com.employee.tracking.entity.PerformanceRating;
import com.employee.tracking.entity.PerformanceReview;
import com.employee.tracking.repository.AppUserRepository;
import com.employee.tracking.repository.EmployeeRepository;
import com.employee.tracking.repository.PerformanceReviewRepository;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PerformanceReviewService {

    private final PerformanceReviewRepository reviewRepository;
    private final EmployeeRepository employeeRepository;
    private final AppUserRepository appUserRepository;

    public PerformanceReviewService(
            PerformanceReviewRepository reviewRepository,
            EmployeeRepository employeeRepository,
            AppUserRepository appUserRepository) {
        this.reviewRepository = reviewRepository;
        this.employeeRepository = employeeRepository;
        this.appUserRepository = appUserRepository;
    }

    public PerformanceReview createReview(
            PerformanceReviewRequest request,
            Long reviewerUserId) {

        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found"));

        if (!employee.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot review an inactive employee");
        }

        if (request.reviewDate().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Review date cannot be in the future");
        }

        validateRatingAndScore(request.rating(), request.score());

        if (reviewRepository.existsByEmployee_IdAndReviewDate(
                request.employeeId(), request.reviewDate())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A review already exists for this employee on this date");
        }

        AppUser reviewer = appUserRepository.findById(reviewerUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reviewer account not found"));

        if (!"ADMIN".equals(reviewer.getRole().name())
                && !"MANAGER".equals(reviewer.getRole().name())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only an admin or manager can create performance reviews");
        }

        PerformanceReview review = new PerformanceReview();
        review.setEmployee(employee);
        review.setReviewer(reviewer);
        review.setRating(request.rating());
        review.setScore(request.score());
        review.setReviewDate(request.reviewDate());
        review.setFeedback(normalizeText(request.feedback()));
        review.setGoals(normalizeText(request.goals()));

        PerformanceReview saved = reviewRepository.save(review);
        initializeReview(saved);
        return saved;
    }

    public List<PerformanceReview> getAllReviews() {
        List<PerformanceReview> reviews =
                reviewRepository.findAllByOrderByReviewDateDesc();
        initializeReviews(reviews);
        return reviews;
    }

    public PerformanceReview getReviewById(Long reviewId) {
        PerformanceReview review = findReview(reviewId);
        initializeReview(review);
        return review;
    }

    public List<PerformanceReview> getReviewsByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Employee not found");
        }

        List<PerformanceReview> reviews =
                reviewRepository
                        .findByEmployee_IdOrderByReviewDateDesc(employeeId);

        initializeReviews(reviews);
        return reviews;
    }

    public List<PerformanceReview> getReviewsByReviewer(Long reviewerId) {
        if (!appUserRepository.existsById(reviewerId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Reviewer account not found");
        }

        List<PerformanceReview> reviews =
                reviewRepository
                        .findByReviewer_IdOrderByReviewDateDesc(reviewerId);

        initializeReviews(reviews);
        return reviews;
    }

    public List<PerformanceReview> getReviewsBetweenDates(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate) {

        if (!employeeRepository.existsById(employeeId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Employee not found");
        }

        if (startDate == null || endDate == null
                || endDate.isBefore(startDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide a valid date range");
        }

        List<PerformanceReview> reviews =
                reviewRepository
                        .findByEmployee_IdAndReviewDateBetweenOrderByReviewDateDesc(
                                employeeId, startDate, endDate);

        initializeReviews(reviews);
        return reviews;
    }

    private void validateRatingAndScore(
            PerformanceRating rating, Integer score) {

        if (rating == null || score == null || score < 1 || score > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rating is required and score must be between 1 and 5");
        }

        int expectedScore = switch (rating) {
            case POOR -> 1;
            case NEEDS_IMPROVEMENT -> 2;
            case MEETS_EXPECTATIONS -> 3;
            case EXCEEDS_EXPECTATIONS -> 4;
            case OUTSTANDING -> 5;
        };

        if (score != expectedScore) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Score must match the selected performance rating");
        }
    }

    private String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private PerformanceReview findReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Performance review not found"));
    }

    private void initializeReviews(List<PerformanceReview> reviews) {
        reviews.forEach(this::initializeReview);
    }

    private void initializeReview(PerformanceReview review) {
        review.getEmployee().getFullName();
        review.getReviewer().getFullName();
    }
}
