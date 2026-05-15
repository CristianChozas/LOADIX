package com.loadix.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.loadix.domain.valueobject.PaymentProvider;
import com.loadix.domain.valueobject.PaymentStatus;

public record LoadPayment(
    UUID id,
    UUID loadId,
    UUID carrierUserId,
    PaymentProvider provider,
    String providerPaymentId,
    BigDecimal amount,
    String currency,
    PaymentStatus status,
    Instant createdAt,
    Instant updatedAt
) {
}
