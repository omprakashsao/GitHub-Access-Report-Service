package com.omprakash.github_access_report.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Global exception handler for the entire application.
 *
 * Any exception thrown from controllers or services
 * will be handled here and converted into a structured
 * HTTP response.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles GithubApiException.
     *
     * Example cases:
     * - Invalid GitHub organization
     * - GitHub API failure
     * - Unauthorized access
     */
    @ExceptionHandler(GithubApiException.class)
    public ResponseEntity<CustomErrorResponse> handleGithubApiException(
            GithubApiException ex,
            HttpServletRequest request) {

        CustomErrorResponse error = new CustomErrorResponse(
                LocalDateTime.now(),
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    /**
     * Handles all unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        CustomErrorResponse error = new CustomErrorResponse(
                LocalDateTime.now(),
                500,
                "Internal Server Error",
                "Unexpected error occurred",
                request.getRequestURI()
        );

        return ResponseEntity.status(500).body(error);
    }
}