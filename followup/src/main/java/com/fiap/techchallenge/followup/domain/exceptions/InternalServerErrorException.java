package com.fiap.techchallenge.followup.domain.exceptions;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends BaseHttpException {
    public InternalServerErrorException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public InternalServerErrorException(String message, Object requestData) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, requestData);
    }
}
