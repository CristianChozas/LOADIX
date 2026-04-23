package com.loadix.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loadix.infrastructure.persistence.entity.WarehouseProfileJpaEntity;

public interface WarehouseProfileJpaRepository extends JpaRepository<WarehouseProfileJpaEntity, UUID> {

    Optional<WarehouseProfileJpaEntity> findByUserId(UUID userId);

}
