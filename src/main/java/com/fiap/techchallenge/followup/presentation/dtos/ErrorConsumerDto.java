package com.fiap.techchallenge.followup.presentation.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ErrorConsumerDto<T> {

    private final Integer errorCode;
    private final String errorDetail;
    private final T rawData;

}
