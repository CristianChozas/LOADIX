package com.loadix.infrastructure.in.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ValidationProbeResponse", description = "Response returned by the validation probe")
public record ValidationProbeResponse(
                @Schema(description = "Human readable message")
                String message,
                @Schema(description = "Echoed name")
                String name,
                @Schema(description = "Echoed email")
                String email) {
}
