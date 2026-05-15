package com.loadix.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "loadix.stripe")
public record StripeProperties(
    String secretKey,
    String defaultTestPaymentMethod,
    String currency
) {
}
