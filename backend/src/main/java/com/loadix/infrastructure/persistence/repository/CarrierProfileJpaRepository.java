package com.loadix.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loadix.infrastructure.persistence.entity.CarrierProfileJpaEntity;

public interface CarrierProfileJpaRepository extends JpaRepository<CarrierProfileJpaEntity, UUID> {

    Optional<CarrierProfileJpaEntity> findByUserId(UUID userId);

}
