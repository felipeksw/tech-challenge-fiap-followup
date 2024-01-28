package com.fiap.techchallenge.followup.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fiap.techchallenge.followup.application.enums.StatusEnum;
import com.fiap.techchallenge.followup.domain.exceptions.InvalidDataException;

@ExtendWith(SpringExtension.class)
class StatusTest {

    private String NULL_STATUS_MESSAGE = "Invalid Status: status must not be null or empty";
    private String UPPERCASE_STATUS_MESSAGE = "Invalid Status: status must not contain uppercase letters";
    private String INVALID_STATUS_MESSAGE = "Invalid Status: status must be in ";

    private Set<String> VALID_STATUS = StatusEnum.getValidStatus();

    @Test
    void givenNullStatus_WhenNewStatus_ThenTrownInvalidData() {
        var receivedException = assertThrowsExactly(InvalidDataException.class, () -> new Status(null),
                "Exception different than expected!");

        assertEquals(NULL_STATUS_MESSAGE, receivedException.getMessage(),
                "Exception message different than expected!");
    }

    @Test
    void givenContainedUpperCase_WhenNewStatus_ThenTrownInvalidData() {
        var receivedException = assertThrowsExactly(InvalidDataException.class, () -> new Status("Recebido"),
                "Exception different than expected!");

        assertEquals(UPPERCASE_STATUS_MESSAGE + "", receivedException.getMessage(),
                "Exception message different than expected!");
    }

    @Test
    void givenInvalidStatus_WhenNewStatus_ThenTrownInvalidData() {
        var receivedException = assertThrowsExactly(InvalidDataException.class, () -> new Status("enviado"),
                "Exception different than expected!");

        assertEquals(INVALID_STATUS_MESSAGE + VALID_STATUS, receivedException.getMessage(),
                "Exception message different than expected!");
    }
}