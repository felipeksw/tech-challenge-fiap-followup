package com.fiap.techchallenge.followup.application.enums;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum StatusEnum {

    RECEBIDO {
        @Override
        public StatusEnum getBeforeStatus() {
            return RECEBIDO;
        }
    },

    EM_PREPARACAO {
        @Override
        public StatusEnum getBeforeStatus() {
            return RECEBIDO;
        }
    },

    PRONTO {
        @Override
        public StatusEnum getBeforeStatus() {
            return EM_PREPARACAO;
        }
    },

    FINALIZADO {
        @Override
        public StatusEnum getBeforeStatus() {
            return PRONTO;
        }
    };

    public static Boolean isValidStatus(String status) {
        return Stream.of(StatusEnum.values()).map(StatusEnum::name).anyMatch(status::equalsIgnoreCase);
    }

    public static Set<String> getValidStatus() {
        return Stream.of(StatusEnum.values()).map(statusEnum -> statusEnum.name().toLowerCase())
                .collect(Collectors.toSet());
    }

    public static List<String> getActiveStatus() {
        Set<StatusEnum> activeStatusEnum = Set.of(RECEBIDO, EM_PREPARACAO, PRONTO);
        return activeStatusEnum.stream().map(statusEnum -> statusEnum.toString().toLowerCase()).toList();

    }

    public static StatusEnum valueOfIgnoreCase(String status) {
        return StatusEnum.valueOf(status.toUpperCase());
    }

    public abstract StatusEnum getBeforeStatus();

}
