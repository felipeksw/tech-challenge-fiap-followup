package com.fiap.techchallenge.followup.gateway.port;

import com.fiap.techchallenge.followup.presentation.dtos.ErrorConsumerDto;

public interface AsynchronousRequestPort {

    void signalPaymentCompleted(Long orderId);

    <T> void sendStatusDl(ErrorConsumerDto<T> errorConsumerDto);

    <T> void sendPaymentPendingDl(ErrorConsumerDto<T> errorConsumerDto);

    <T> void sendPaymentRequestedDl(ErrorConsumerDto<T> errorConsumerDto);

    <T> void sendPaymentRefusedDl(ErrorConsumerDto<T> errorConsumerDto);

    <T> void sendPaymentAcceptedDl(ErrorConsumerDto<T> errorConsumerDto);
}
