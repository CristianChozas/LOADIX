package com.loadix.infrastructure.in.web.response;

public record ValidationProbeResponse(
                String message,
                String name,
                String email) {
}
