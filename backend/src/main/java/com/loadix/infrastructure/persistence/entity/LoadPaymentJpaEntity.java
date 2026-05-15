package com.loadix.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.loadix.domain.model.LoadPayment;
import com.loadix.domain.valueobject.PaymentProvider;
import com.loadix.domain.valueobject.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "load_payments")
public class LoadPaymentJpaEntity extends BaseJpaEntity {

    @Column(name = "load_id", nullable = false)
    private UUID loadId;

    @Column(name = "carrier_user_id", nullable = false)
    private UUID carrierUserId;

    @Column(name = "provider", nullable = false, length = 32)
    private String provider;

    @Column(name = "provider_payment_id", nullable = false, length = 128)
    private String providerPaymentId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    protected LoadPaymentJpaEntity() {
    }

    public static LoadPaymentJpaEntity fromDomain(LoadPayment payment) {
        LoadPaymentJpaEntity entity = new LoadPaymentJpaEntity();
        entity.loadId = payment.loadId();
        entity.carrierUserId = payment.carrierUserId();
        entity.provider = payment.provider().name();
        entity.providerPaymentId = payment.providerPaymentId();
        entity.amount = payment.amount();
        entity.currency = payment.currency();
        entity.status = payment.status().name();
        return entity;
    }

    public LoadPayment toDomain() {
        return new LoadPayment(
            getId(),
            loadId,
            carrierUserId,
            PaymentProvider.valueOf(provider),
            providerPaymentId,
            amount,
            currency,
            PaymentStatus.valueOf(status),
            getCreatedAt(),
            getUpdatedAt()
        );
    }
}
