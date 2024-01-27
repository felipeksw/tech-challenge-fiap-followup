package com.fiap.techchallenge.followup.presentation.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class OrderUpdateStatusResquestDto {

    private long orderId;
    private String status;
}
