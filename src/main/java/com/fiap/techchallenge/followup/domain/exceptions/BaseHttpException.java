package com.fiap.techchallenge.followup.domain.exceptions;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

public class BaseHttpException extends RuntimeException {

    private final HttpStatus statusCode;

    private final RequestDataDto requestData;

    public BaseHttpException(HttpStatus statusCode, String message, RequestDataDto requestData) {
        super(message);
        this.statusCode = statusCode;
        this.requestData = requestData;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public RequestDataDto getRequestData() {
        return requestData;
    }

    public record RequestDataDto(
            Object requestData) implements Serializable {
    }
}
