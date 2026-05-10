package com.loadix.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.loadix.application.port.out.CarrierProfilePort;
import com.loadix.domain.model.CarrierProfile;
import com.loadix.infrastructure.persistence.entity.CarrierProfileJpaEntity;
import com.loadix.infrastructure.persistence.repository.CarrierProfileJpaRepository;

@Component
public class CarrierProfilePersistenceAdapter implements CarrierProfilePort {

    private final CarrierProfileJpaRepository carrierProfileJpaRepository;

    public CarrierProfilePersistenceAdapter(CarrierProfileJpaRepository carrierProfileJpaRepository) {
        this.carrierProfileJpaRepository = carrierProfileJpaRepository;
    }

    @Override
    public CarrierProfile save(CarrierProfile profile) {
        CarrierProfileJpaEntity entity = CarrierProfileJpaEntity.fromDomain(profile);
        carrierProfileJpaRepository.findByUserId(profile.id())
                .ifPresent(existing -> entity.reusePersistentId(existing.getId()));
        CarrierProfileJpaEntity persisted = carrierProfileJpaRepository.save(entity);
        return persisted.toDomain();
    }

    @Override
    public Optional<CarrierProfile> findByUserId(UUID userId) {
        return carrierProfileJpaRepository.findByUserId(userId)
                .map(CarrierProfileJpaEntity::toDomain);
    }
}
