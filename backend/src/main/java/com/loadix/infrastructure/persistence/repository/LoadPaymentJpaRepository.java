package com.loadix.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loadix.infrastructure.persistence.entity.LoadPaymentJpaEntity;

public interface LoadPaymentJpaRepository extends JpaRepository<LoadPaymentJpaEntity, UUID> {

    Optional<LoadPaymentJpaEntity> findFirstByLoadIdAndCarrierUserIdAndStatusOrderByCreatedAtDesc(
        UUID loadId,
        UUID carrierUserId,
        String status
    );

    long countByCarrierUserIdAndStatus(UUID carrierUserId, String status);
}
