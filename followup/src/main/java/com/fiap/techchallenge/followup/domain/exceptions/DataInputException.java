package com.fiap.techchallenge.followup.domain.exceptions;

import org.springframework.http.HttpStatus;

public class DataInputException extends BaseHttpException {
    public DataInputException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}