package com.zx1316.memesmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResourceNotFoundException extends ResponseStatusException {
    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public ResourceNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}
