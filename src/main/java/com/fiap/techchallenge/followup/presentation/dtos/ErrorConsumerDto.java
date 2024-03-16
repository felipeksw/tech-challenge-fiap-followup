package com.fiap.techchallenge.followup.presentation.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Builder.Default;

@AllArgsConstructor
@Getter
@Builder
public class ErrorConsumerDto<T> {

    @Default
    private final String errorSource = "tech-challenge-fiap-followup";
    private final Integer errorCode;
    private final String errorDetail;
    private final T rawData;

}
