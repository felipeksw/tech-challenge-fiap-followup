package com.fiap.techchallenge.followup.domain.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseHttpException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(String message, Object requestData) {
        super(HttpStatus.BAD_REQUEST, message, requestData);
    }
}