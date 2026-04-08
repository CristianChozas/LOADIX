package com.loadix.infrastructure.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "loadix.rate-limit")
public record RateLimitProperties(
        int publicCapacity,
        Duration publicWindow,
        int authCapacity,
        Duration authWindow
) {
}
