// Centralized @RestControllerAdvice that every service picks up
// automatically via component scanning (as long as com.helix.common
// is included in the scan base package - each service's main class
// package is com.helix.<service>, and we configure @SpringBootApplication
// scanBasePackages to include "com.helix" so this common package is found).
//
// Converts thrown exceptions into a consistent ErrorResponse JSON body
// instead of Spring's default whitelabel error page.

package com.helix.common.exception;

import com.helix.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Catches our own custom exceptions (ResourceNotFoundException,
    // UnauthorizedException, etc.) and uses the status they carry.
    @ExceptionHandler(HelixException.class)
    public ResponseEntity<ErrorResponse> handleHelixException(HelixException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                ex.getStatus().value(),
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    // Catch-all safety net for anything unexpected, so we never leak
    // a raw stack trace to the client. Returns 500.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred: " + ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}