package com.loadix.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.loadix.infrastructure.config.StripeProperties;

@Configuration
@EnableConfigurationProperties(StripeProperties.class)
public class PaymentConfig {
}
