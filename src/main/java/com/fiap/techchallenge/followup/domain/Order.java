package com.fiap.techchallenge.followup.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;

public record Order(Long id, Status status, LocalDateTime createdAt) {

    @Builder
    public Order(Long id, String status, LocalDateTime createdAt) {
        this(id, new Status(status), createdAt);
    }
}
