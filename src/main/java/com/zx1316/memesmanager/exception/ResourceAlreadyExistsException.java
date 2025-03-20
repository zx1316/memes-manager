package com.zx1316.memesmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResourceAlreadyExistsException extends ResponseStatusException {
    public ResourceAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }

    public ResourceAlreadyExistsException() {
        super(HttpStatus.CONFLICT);
    }
}
