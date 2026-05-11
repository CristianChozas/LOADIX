package com.loadix.infrastructure.persistence.adapter;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.loadix.application.port.out.LoadPort;
import com.loadix.domain.model.LoadPageResult;
import com.loadix.domain.model.LoadPublication;
import com.loadix.domain.model.PersistedLoadPublication;
import com.loadix.infrastructure.persistence.entity.LoadJpaEntity;
import com.loadix.infrastructure.persistence.repository.LoadJpaRepository;

@Component
public class LoadPersistenceAdapter implements LoadPort {

    private final LoadJpaRepository loadJpaRepository;

    public LoadPersistenceAdapter(LoadJpaRepository loadJpaRepository) {
        this.loadJpaRepository = loadJpaRepository;
    }

    @Override
    public PersistedLoadPublication save(LoadPublication loadPublication) {
        LoadJpaEntity entity = LoadJpaEntity.fromDomain(loadPublication);
        LoadJpaEntity persisted = loadJpaRepository.save(entity);
        return persisted.toDomain();
    }

    @Override
    public LoadPageResult findByWarehouseUserId(
        UUID warehouseUserId,
        int page,
        int size,
        boolean sortAsc,
        LocalDate pickupDateFrom,
        LocalDate pickupDateTo
    ) {
        Sort sort = sortAsc
            ? Sort.by(Sort.Direction.ASC, "createdAt")
            : Sort.by(Sort.Direction.DESC, "createdAt");

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<LoadJpaEntity> specification = byWarehouseUserId(warehouseUserId)
            .and(byPickupDateFrom(pickupDateFrom))
            .and(byPickupDateTo(pickupDateTo));

        Page<LoadJpaEntity> result = loadJpaRepository.findAll(specification, pageable);

        return new LoadPageResult(
            result.getContent().stream().map(LoadJpaEntity::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.hasNext(),
            result.hasPrevious()
        );
    }

    @Override
    public PersistedLoadPublication updateById(UUID loadId, LoadPublication loadPublication) {
        LoadJpaEntity existing = loadJpaRepository.findById(loadId)
            .orElseThrow();

        existing.updateFromDomain(loadPublication);
        LoadJpaEntity persisted = loadJpaRepository.save(existing);
        return persisted.toDomain();
    }

    @Override
    public Optional<PersistedLoadPublication> findById(UUID loadId) {
        return loadJpaRepository.findById(loadId).map(LoadJpaEntity::toDomain);
    }

    private Specification<LoadJpaEntity> byWarehouseUserId(UUID warehouseUserId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("warehouseUserId"), warehouseUserId);
    }

    private Specification<LoadJpaEntity> byPickupDateFrom(LocalDate pickupDateFrom) {
        if (pickupDateFrom == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("pickupDate"), pickupDateFrom);
    }

    private Specification<LoadJpaEntity> byPickupDateTo(LocalDate pickupDateTo) {
        if (pickupDateTo == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("pickupDate"), pickupDateTo);
    }
}
