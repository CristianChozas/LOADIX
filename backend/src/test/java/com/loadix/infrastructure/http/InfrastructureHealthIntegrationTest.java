package com.loadix.infrastructure.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InfrastructureHealthIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void returnsHealthPayload() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/v1/health", Map.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).containsEntry("status", "ok");
        assertThat(response.getBody()).containsEntry("service", "loadix-backend-api");
        assertThat(response.getBody()).containsEntry("version", "0.0.1-SNAPSHOT");
    }
}
