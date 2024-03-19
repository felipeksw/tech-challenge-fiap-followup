package com.fiap.techchallenge.followup.presentation.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fiap.techchallenge.followup.domain.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Builder
public class OrderDto {

    private long orderId;
    private String status;
    private LocalDateTime createdAt;

    public static OrderDto of(Order order) {
        return OrderDto.builder()
                .orderId(order.id())
                .status(order.status().value())
                .createdAt(order.createdAt())
                .build();
    }
}
