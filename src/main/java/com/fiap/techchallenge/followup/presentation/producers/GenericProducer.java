package com.fiap.techchallenge.followup.presentation.producers;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenericProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public <T> void send(String topicName, T objectToSend) {
        try {
            kafkaTemplate.send(topicName, objectMapper.writeValueAsString(objectToSend));
        } catch (Exception e) {
            log.error("Error:{}. While sending the message:{}. To the topic: {}", e.getMessage(), objectToSend,
                    topicName);
        }
    }
}
