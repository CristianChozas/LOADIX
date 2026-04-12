package com.loadix.infrastructure.in.web.response;

public record HealthResponse(
                String status,
                String service,
                String version) {
}
