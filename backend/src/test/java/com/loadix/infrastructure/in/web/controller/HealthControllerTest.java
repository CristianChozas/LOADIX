package com.loadix.infrastructure.in.web.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import com.loadix.support.IntegrationTestContainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthControllerTest extends IntegrationTestContainers {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @SuppressWarnings("unchecked")
    void returnsHealthPayload() {
        ResponseEntity<?> response = restTemplate.getForEntity("/api/v1/health", Map.class);
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(body).containsEntry("status", "ok");
        assertThat(body).containsEntry("service", "loadix-backend-api");
        assertThat(body).containsEntry("version", "0.0.1-SNAPSHOT");
    }
}
