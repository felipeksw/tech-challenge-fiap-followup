package com.fiap.techchallenge.followup.presentation.dtos;

import java.time.LocalDate;

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
public class OrderDto {

    private long orderId;
    private String status;
    private LocalDate createdAt;

    public static OrderDto of(Order order) {
        return OrderDto.builder()
                .orderId(order.id())
                .status(order.status().value())
                .createdAt(order.createdAt())
                .build();
    }
}