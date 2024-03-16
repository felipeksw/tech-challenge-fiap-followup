package com.fiap.techchallenge.followup.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.techchallenge.followup.application.enums.StatusEnum;
import com.fiap.techchallenge.followup.util.StatusValidatorUtil;

public record Status(String value) {

    public Status {
        StatusValidatorUtil.validate(value);
    }

    public Boolean newStatusIsValid(Status newStatus) {
        StatusEnum actualStatusEnum = StatusEnum.valueOfIgnoreCase(value);
        StatusEnum newStatusEnum = StatusEnum.valueOfIgnoreCase(newStatus.value);

        return newStatusEnum.getPermittedBeforeStatus().contains(actualStatusEnum);

    }

    @JsonIgnore
    public Boolean isPaymentStatus() {

        StatusEnum statusEnum = StatusEnum.valueOfIgnoreCase(value);

        return StatusEnum.getOrderPaymentStatus().contains(statusEnum);
    }
}
