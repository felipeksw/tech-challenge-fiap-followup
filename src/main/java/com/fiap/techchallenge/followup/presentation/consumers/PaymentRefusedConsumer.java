package com.fiap.techchallenge.followup.presentation.consumers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techchallenge.followup.application.enums.StatusEnum;
import com.fiap.techchallenge.followup.application.useCases.OrderUseCases;
import com.fiap.techchallenge.followup.domain.exceptions.BaseHttpException;
import com.fiap.techchallenge.followup.presentation.dtos.ErrorConsumerDto;
import com.fiap.techchallenge.followup.presentation.dtos.OrderPaymentConsumerDto;
import com.fiap.techchallenge.followup.presentation.dtos.OrderUpdateStatusResquestDto;
import com.fiap.techchallenge.followup.presentation.producers.GenericProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentRefusedConsumer {

    private final ObjectMapper objectMapper;
    private final OrderUseCases orderUseCases;
    private final GenericProducer genericProducer;

    @Value("${kafka.topic.payment-refused.dl}")
    private String paymentRefusedDlTopic;

    @KafkaListener(topics = "${kafka.topic.payment-refused}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(@Payload String message) throws JsonProcessingException {
        try {
            OrderPaymentConsumerDto orderPaymentUpdate = objectMapper.readValue(message, OrderPaymentConsumerDto.class);

            OrderUpdateStatusResquestDto orderUpdateStatusResquestDto = OrderUpdateStatusResquestDto.builder()
                    .orderId(orderPaymentUpdate.getOrderId())
                    .status(StatusEnum.PAYMENT_REFUSED.name().toLowerCase())
                    .build();
            orderUseCases.updateStatus(orderUpdateStatusResquestDto);

        } catch (BaseHttpException e) {
            ErrorConsumerDto<Object> errorConsumer = ErrorConsumerDto.builder()
                    .errorCode(e.getStatusCode().value())
                    .errorDetail(e.getMessage())
                    .rawData(message)
                    .build();
            genericProducer.send(paymentRefusedDlTopic, errorConsumer);
        } catch (Exception e) {
            log.error("Erro processing the message {} on the {}: {} ", message, this.getClass().getSimpleName(),
                    e.getMessage());
        }
    }
}
