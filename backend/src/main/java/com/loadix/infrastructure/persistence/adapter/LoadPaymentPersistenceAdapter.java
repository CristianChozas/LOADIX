package com.loadix.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.loadix.application.port.out.LoadPaymentPort;
import com.loadix.domain.model.LoadPayment;
import com.loadix.domain.valueobject.PaymentStatus;
import com.loadix.infrastructure.persistence.entity.LoadPaymentJpaEntity;
import com.loadix.infrastructure.persistence.repository.LoadPaymentJpaRepository;

@Component
public class LoadPaymentPersistenceAdapter implements LoadPaymentPort {

    private final LoadPaymentJpaRepository repository;

    public LoadPaymentPersistenceAdapter(LoadPaymentJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public LoadPayment save(LoadPayment payment) {
        LoadPaymentJpaEntity persisted = repository.save(LoadPaymentJpaEntity.fromDomain(payment));
        return persisted.toDomain();
    }

    @Override
    public Optional<LoadPayment> findByLoadIdAndCarrierUserIdAndStatus(UUID loadId, UUID carrierUserId, PaymentStatus status) {
        return repository.findFirstByLoadIdAndCarrierUserIdAndStatusOrderByCreatedAtDesc(loadId, carrierUserId, status.name())
            .map(LoadPaymentJpaEntity::toDomain);
    }

    @Override
    public long countByCarrierUserIdAndStatus(UUID carrierUserId, PaymentStatus status) {
        return repository.countByCarrierUserIdAndStatus(carrierUserId, status.name());
    }
}
