package com.loadix.infrastructure.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.loadix.application.port.out.WarehouseProfilePort;
import com.loadix.domain.model.WarehouseProfile;
import com.loadix.infrastructure.persistence.entity.WarehouseProfileJpaEntity;
import com.loadix.infrastructure.persistence.repository.WarehouseProfileJpaRepository;

@Component
public class WarehouseProfilePersistenceAdapter implements WarehouseProfilePort {
    private final WarehouseProfileJpaRepository warehouseProfileJpaRepository;

    public WarehouseProfilePersistenceAdapter(WarehouseProfileJpaRepository warehouseProfileJpaRepository) {
        this.warehouseProfileJpaRepository = warehouseProfileJpaRepository;
    }

    @Override
    public WarehouseProfile save(WarehouseProfile profile) {
        WarehouseProfileJpaEntity entity = WarehouseProfileJpaEntity.fromDomain(profile);

        warehouseProfileJpaRepository.findByUserId(profile.userId())
                .ifPresent(existing -> entity.reusePersistentId(existing.getId()));

        WarehouseProfileJpaEntity persisted = warehouseProfileJpaRepository.save(entity);
        return persisted.toDomain();
    }

    @Override
    public Optional<WarehouseProfile> findByUserId(java.util.UUID id) {
        return warehouseProfileJpaRepository.findByUserId(id)
                .map(WarehouseProfileJpaEntity::toDomain);
    }
}
