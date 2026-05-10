package com.loadix.infrastructure.in.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HealthResponse", description = "Service health payload")
public record HealthResponse(
                @Schema(description = "Health status")
                String status,
                @Schema(description = "Service name")
                String service,
                @Schema(description = "Service version")
                String version) {
}
