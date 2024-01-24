package com.fiap.techchallenge.followup.presentation.controllers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    private static final Logger LOG = LoggerFactory.getLogger(HealthController.class);

    @GetMapping
    public Map<String, String> healthCheck() {
        var response = Map.of("status", "OK");
        LOG.info("Endpoint de Health: {}", response);
        return response;
    }
}