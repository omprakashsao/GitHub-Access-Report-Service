package com.omprakash.github_access_report.exception;

import java.time.LocalDateTime;
/**
 * Custom error response model returned to the client
 * when an exception occurs in the application.
 *
 * This helps provide structured error information
 * instead of plain error messages.
 */



public class CustomErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public CustomErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path) {

        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
