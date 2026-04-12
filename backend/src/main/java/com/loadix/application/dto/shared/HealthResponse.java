package com.loadix.application.dto.shared;

public record HealthResponse(
                String status,
                String service,
                String version) {
}
