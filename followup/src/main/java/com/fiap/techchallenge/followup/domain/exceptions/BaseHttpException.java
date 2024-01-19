package com.fiap.techchallenge.followup.domain.exceptions;

import org.springframework.http.HttpStatus;

public class BaseHttpException extends RuntimeException {

    private HttpStatus statusCode;

    protected Object requestData;

    public BaseHttpException(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public BaseHttpException(HttpStatus statusCode, String message, Object requestData) {
        super(message);
        this.statusCode = statusCode;
        this.requestData = requestData;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public Object getRequestData() {
        return requestData;
    }
}
