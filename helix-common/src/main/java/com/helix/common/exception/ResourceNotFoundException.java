// Thrown when a lookup (account, transaction, card, etc.) fails to find
// a matching record. Maps to HTTP 404.

package com.helix.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends HelixException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}