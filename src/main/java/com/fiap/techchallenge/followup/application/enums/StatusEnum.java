package com.fiap.techchallenge.followup.application.enums;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum StatusEnum {

    NEW {
        @Override
        public Set<StatusEnum> getPermittedBeforeStatus() {
            return Collections.emptySet();
        }
    },

    PAYMENT_REQUESTED {
        @Override
        public Set<StatusEnum> getPermittedBeforeStatus() {
            return Set.of(NEW, PAYMENT_REFUSED);
        }
    },

    PAYMENT_PENDING {
        @Override
        public Set<StatusEnum> getPermittedBeforeStatus() {
            return Set.of(PAYMENT_REQUESTED);
        }
    },

    PAYMENT_ACCEPTED {
        @Override
        public Set<StatusEnum> getPermittedBeforeStatus() {
            return Set.of(PAYMENT_REQUESTED, PAYMENT_PENDING);
        }
    },

    PAYMENT_REFUSED {
        @Override
        public Set<StatusEnum> getPermittedBeforeStatus() {
            return Set.of(PAYMENT_REQUESTED, PAYMENT_PENDING);
        }
    },

    ORDER_RECEIVED {
        @Override
        public Set<StatusEnum> getPermittedBeforeStatus() {
            return Set.of(PAYMENT_ACCEPTED);
        }
    },

    ORDER_IN_PRODUCTION {
        @Override
        public Set<StatusEnum> getPermittedBeforeStatus() {
            return Set.of(ORDER_RECEIVED);
        }
    },

    ORDER_COMPLETED {
        @Override
        public Set<StatusEnum> getPermittedBeforeStatus() {
            return Set.of(ORDER_IN_PRODUCTION);
        }
    },

    ORDER_DELIVERED {
        @Override
        public Set<StatusEnum> getPermittedBeforeStatus() {
            return Set.of(ORDER_COMPLETED);
        }
    };

    public static Boolean isValidStatus(String status) {
        return Stream.of(StatusEnum.values()).map(StatusEnum::name).anyMatch(status::equalsIgnoreCase);
    }

    public static Set<String> getValidStatus() {
        return Stream.of(StatusEnum.values()).map(statusEnum -> statusEnum.name().toLowerCase())
                .collect(Collectors.toSet());
    }

    public static List<String> getActiveOrderStatus() {
        Set<StatusEnum> activeStatusEnum = Set.of(ORDER_RECEIVED, ORDER_IN_PRODUCTION, ORDER_COMPLETED);
        return activeStatusEnum.stream().map(statusEnum -> statusEnum.toString().toLowerCase()).toList();

    }

    public static List<StatusEnum> getOrderPaymentStatus() {
        return List.of(NEW, PAYMENT_REQUESTED, PAYMENT_ACCEPTED, PAYMENT_REFUSED);

    }

    public static StatusEnum valueOfIgnoreCase(String status) {
        return StatusEnum.valueOf(status.toUpperCase());
    }

    public abstract Set<StatusEnum> getPermittedBeforeStatus();

}
