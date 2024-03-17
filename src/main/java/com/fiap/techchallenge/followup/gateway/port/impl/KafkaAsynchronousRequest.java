package com.fiap.techchallenge.followup.gateway.port.impl;

import java.util.Map;

import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techchallenge.followup.gateway.port.AsynchronousRequestPort;
import com.fiap.techchallenge.followup.presentation.dtos.ErrorConsumerDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaAsynchronousRequest implements AsynchronousRequestPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.payment-completed}")
    private String paymentCompletedTopic;

    @Value("${kafka.topic.order-status.dl}")
    private String orderStatusDlTopic;

    @Value("${kafka.topic.payment-pending.dl}")
    private String paymentPendingDlTopic;

    @Value("${kafka.topic.payment-accepted.dl}")
    private String paymentAcceptedDlTopic;

    @Value("${kafka.topic.payment-refused.dl}")
    private String paymentRefusedDlTopic;

    @Value("${kafka.topic.payment-requested.dl}")
    private String paymentRequestDlTopic;

    private <T> void send(String topicName, T objectToSend) {
        try {
            kafkaTemplate.send(topicName, objectMapper.writeValueAsString(objectToSend));
        } catch (Exception e) {
            log.error("Error:{}. While sending the message:{}. To the topic: {}", e.getMessage(), objectToSend,
                    topicName);
        }
    }

    @Override
    public void signalPaymentCompleted(Long orderId) {
        Map<String, Long> orderIdWithPaymentCompleted = Map.of("orderId", orderId);
        send(paymentCompletedTopic, orderIdWithPaymentCompleted);

    }

    @Override
    public <T> void sendStatusDl(ErrorConsumerDto<T> errorConsumerDto) {
        send(orderStatusDlTopic, errorConsumerDto);
    }

    @Override
    public <T> void sendPaymentPendingDl(ErrorConsumerDto<T> errorConsumerDto) {
        send(paymentPendingDlTopic, errorConsumerDto);
    }

    @Override
    public <T> void sendPaymentRequestedDl(ErrorConsumerDto<T> errorConsumerDto) {
        send(paymentRequestDlTopic, errorConsumerDto);
    }

    @Override
    public <T> void sendPaymentRefusedDl(ErrorConsumerDto<T> errorConsumerDto) {
        send(paymentRefusedDlTopic, errorConsumerDto);
    }

    @Override
    public <T> void sendPaymentAcceptedDl(ErrorConsumerDto<T> errorConsumerDto) {
        send(paymentAcceptedDlTopic, errorConsumerDto);
    }

}
