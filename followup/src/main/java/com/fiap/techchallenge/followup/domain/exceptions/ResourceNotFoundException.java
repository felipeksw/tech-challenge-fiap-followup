package com.fiap.techchallenge.followup.domain.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseHttpException {
    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public ResourceNotFoundException(String message, Object requestData) {
        super(HttpStatus.NOT_FOUND, message, requestData);
    }
}
