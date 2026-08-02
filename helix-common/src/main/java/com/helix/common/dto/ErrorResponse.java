// Standard error-response envelope returned by GlobalExceptionHandler.
// Includes an HTTP status code, a machine-readable error code-ish message,
// a timestamp, and the request path - useful for debugging on the frontend
// and in logs across services.

package com.helix.common.dto;

import java.time.Instant;

public class ErrorResponse {

    private int status;
    private String message;
    private String path;
    private Instant timestamp;

    public ErrorResponse() {
    }

    public ErrorResponse(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = Instant.now();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}