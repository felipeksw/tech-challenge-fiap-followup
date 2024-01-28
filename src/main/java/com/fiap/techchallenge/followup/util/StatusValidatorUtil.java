package com.fiap.techchallenge.followup.util;

import com.fiap.techchallenge.followup.application.enums.StatusEnum;
import com.fiap.techchallenge.followup.domain.exceptions.InvalidDataException;
import com.google.common.base.Strings;

public class StatusValidatorUtil {

    private final static String INVALID_STATUS = "Invalid Status";

    private StatusValidatorUtil() {

    }

    public static void validate(String status) {
        if (Strings.isNullOrEmpty(status)) {
            throw new InvalidDataException(INVALID_STATUS + ": status must not be null or empty");
        }

        if (!status.equals(status.toLowerCase())) {
            throw new InvalidDataException(INVALID_STATUS + ": status must not contain uppercase letters");
        }

        if (!StatusEnum.isValidStatus(status)) {
            throw new InvalidDataException(INVALID_STATUS + ": status must be in " + StatusEnum.getValidStatus());
        }
    }
}
