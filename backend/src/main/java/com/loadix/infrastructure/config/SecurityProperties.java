package com.loadix.infrastructure.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "loadix.security")
public record SecurityProperties(
        List<String> allowedOrigins,
        String jwtSecret,
        Duration jwtExpiration
) {
}
