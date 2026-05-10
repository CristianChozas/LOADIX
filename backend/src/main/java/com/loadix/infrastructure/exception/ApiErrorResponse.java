package com.loadix.infrastructure.exception;

import java.time.Instant;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiErrorResponse", description = "Standard API error payload")
public record ApiErrorResponse(
        @Schema(description = "Timestamp of the error")
        Instant timestamp,
        @Schema(description = "HTTP status code")
        int status,
        @Schema(description = "HTTP error reason")
        String error,
        @Schema(description = "Application error code")
        String code,
        @Schema(description = "Human readable error message")
        String message,
        @Schema(description = "Request path")
        String path,
        @Schema(description = "Field validation violations")
        List<FieldViolation> violations
) {
    @Schema(name = "FieldViolation", description = "Single field validation violation")
    public record FieldViolation(String field, String message) {
    }
}
