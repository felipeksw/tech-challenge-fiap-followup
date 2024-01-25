package com.fiap.techchallenge.followup.presentation.dtos;

import com.fiap.techchallenge.followup.domain.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderUpdateStatusResponseDto {

    private long orderId;
    private String status;
    private String message;

    public static OrderUpdateStatusResponseDto of(Order order, String message) {
        return OrderUpdateStatusResponseDto.builder()
                .orderId(order.id())
                .status(order.status().value())
                .message(message)
                .build();
    }
}
