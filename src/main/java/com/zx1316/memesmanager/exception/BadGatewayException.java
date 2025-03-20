package com.zx1316.memesmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadGatewayException extends ResponseStatusException {
    public BadGatewayException(String message) {
        super(HttpStatus.BAD_GATEWAY, message);
    }

    public BadGatewayException() {
        super(HttpStatus.BAD_GATEWAY);
    }
}
