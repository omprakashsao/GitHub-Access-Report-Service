package com.omprakash.github_access_report.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception used when any error occurs
 * while communicating with the GitHub API.
 *
 * Example cases:
 * - Invalid organization
 * - GitHub API rate limit exceeded
 * - Unauthorized GitHub token
 */
public class GithubApiException extends RuntimeException {

    /**
     * Default constructor
     */
    public GithubApiException() {
    }

    /**
     * Constructor with custom message
     *
     * @param message error message
     */
    public GithubApiException(String message) {
        super(message);
    }

    private HttpStatus status;

    public GithubApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
