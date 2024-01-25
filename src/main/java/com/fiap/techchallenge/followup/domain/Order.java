package com.fiap.techchallenge.followup.domain;

import java.time.LocalDate;

import lombok.Builder;

public record Order(Long id, Status status, LocalDate createdAt) {

    @Builder
    public Order(Long id, String status, LocalDate createdAt) {
        this(id, new Status(status), createdAt);
    }
}
