// Thrown for authentication/authorization failures - e.g. wrong password
// on login, or an account trying to act on data it doesn't own.
// Maps to HTTP 401.

package com.helix.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends HelixException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}