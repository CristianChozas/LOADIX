package com.loadix.application.dto.common;

public record HealthResponse(
        String status,
        String service,
        String version
) {
}
