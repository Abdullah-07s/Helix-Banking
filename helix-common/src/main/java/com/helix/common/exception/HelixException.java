// Base unchecked exception for all Helix-specific business errors.
// Every custom exception in the project extends this, so
// GlobalExceptionHandler can catch it as a single case if needed
// while still allowing specific subclasses their own HTTP status mapping.

package com.helix.common.exception;

import org.springframework.http.HttpStatus;

public class HelixException extends RuntimeException {

    private final HttpStatus status;

    public HelixException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}