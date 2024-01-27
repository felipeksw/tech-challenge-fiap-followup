package com.fiap.techchallenge.followup.domain.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseHttpException {

    public BadRequestException(String message, RequestDataDto requestData) {
        super(HttpStatus.BAD_REQUEST, message, requestData);
    }
}