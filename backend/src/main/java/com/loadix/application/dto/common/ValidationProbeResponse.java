package com.loadix.application.dto.common;

public record ValidationProbeResponse(
        String message,
        String name,
        String email
) {
}
