package com.fiap.techchallenge.followup.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fiap.techchallenge.followup.domain.exceptions.BaseHttpException;
import com.fiap.techchallenge.followup.presentation.dtos.ErrorResponseDto;

@RestControllerAdvice
public class HttpExceptionHandler {

    @ExceptionHandler(BaseHttpException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpException(BaseHttpException e) {
        return ResponseEntity.status(e.getStatusCode()).body(new ErrorResponseDto(e.getMessage()));
    }

}
