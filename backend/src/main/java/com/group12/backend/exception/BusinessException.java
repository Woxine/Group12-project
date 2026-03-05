package com.group12.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception class for business logic errors.
 * Use this exception when a specific business rule is violated (e.g., "Insufficient balance", "Invalid status").
 * It allows specifying an HTTP status code to return to the client, 
 * providing more granular control than standard RuntimeExceptions.
 */
public class BusinessException extends RuntimeException {
    private final HttpStatus status;

    public BusinessException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
