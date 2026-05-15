package com.loadix.infrastructure.persistence.adapter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.loadix.application.port.out.LoadPort;
import com.loadix.domain.model.AvailableLoadsFilters;
import com.loadix.domain.model.CargoTypeCount;
import com.loadix.domain.model.LoadPageResult;
import com.loadix.domain.model.LoadPublication;
import com.loadix.domain.model.LoadStatusCount;
import com.loadix.domain.model.PersistedLoadPublication;
import com.loadix.domain.valueobject.CargoType;
import com.loadix.domain.valueobject.LoadStatus;
import com.loadix.infrastructure.persistence.entity.LoadJpaEntity;
import com.loadix.infrastructure.persistence.repository.LoadJpaRepository;

@Component
public class LoadPersistenceAdapter implements LoadPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadPersistenceAdapter.class);

    private final LoadJpaRepository loadJpaRepository;
    private final JdbcTemplate jdbcTemplate;

    public LoadPersistenceAdapter(LoadJpaRepository loadJpaRepository, JdbcTemplate jdbcTemplate) {
        this.loadJpaRepository = loadJpaRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PersistedLoadPublication save(LoadPublication loadPublication) {
        LOGGER.info(
            "LOAD_SAVE_REQUEST table=loadix.loads warehouse_user_id={} created_by_user_id={} origin_city={} destination_city={} status={}",
            loadPublication.warehouseUserId(),
            loadPublication.createdByUserId(),
            loadPublication.originCity(),
            loadPublication.destinationCity(),
            loadPublication.status()
        );

        LoadJpaEntity entity = LoadJpaEntity.fromDomain(loadPublication);
        LoadJpaEntity persisted = loadJpaRepository.save(entity);
        PersistedLoadPublication saved = persisted.toDomain();

        LOGGER.info(
            "LOAD_SAVE_RESULT table=loadix.loads db_fingerprint=\"{}\" id={} warehouse_user_id={} created_by_user_id={} status={} created_at={} updated_at={}",
            dbFingerprint(),
            saved.id(),
            saved.warehouseUserId(),
            saved.createdByUserId(),
            saved.status(),
            saved.createdAt(),
            saved.updatedAt()
        );

        return saved;
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
        List<PersistedLoadPublication> items = result.getContent().stream().map(LoadJpaEntity::toDomain).toList();

        LOGGER.info(
            "LOAD_FIND_MINE table=loadix.loads db_fingerprint=\"{}\" warehouse_user_id={} page={} size={} sort={} pickup_date_from={} pickup_date_to={} total_elements={} returned_ids={}",
            dbFingerprint(),
            warehouseUserId,
            page,
            size,
            sortAsc ? "createdAt:asc" : "createdAt:desc",
            pickupDateFrom,
            pickupDateTo,
            result.getTotalElements(),
            items.stream().map(load -> load.id().toString()).toList()
        );

        return new LoadPageResult(
            items,
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.hasNext(),
            result.hasPrevious()
        );
    }

    @Override
    public LoadPageResult findAvailableLoads(int page, int size, boolean sortAsc, AvailableLoadsFilters filters) {
        Sort sort = sortAsc
            ? Sort.by(Sort.Direction.ASC, "createdAt")
            : Sort.by(Sort.Direction.DESC, "createdAt");

        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<LoadJpaEntity> specification = byStatus(LoadStatus.PUBLISHED)
            .and(byQuery(filters.query()))
            .and(byOrigin(filters.origin()))
            .and(byDestination(filters.destination()))
            .and(byPickupDate(filters.pickupDate()))
            .and(byPalletsMin(filters.palletsMin()))
            .and(byPalletsMax(filters.palletsMax()))
            .and(byWeightKgMin(filters.weightKgMin()))
            .and(byWeightKgMax(filters.weightKgMax()))
            .and(byCargoType(filters.cargoType()))
            .and(byPriceMin(filters.priceMin()))
            .and(byPriceMax(filters.priceMax()));

        Page<LoadJpaEntity> result = loadJpaRepository.findAll(specification, pageable);
        List<PersistedLoadPublication> items = result.getContent().stream().map(LoadJpaEntity::toDomain).toList();

        LOGGER.info(
            "LOAD_FIND_AVAILABLE table=loadix.loads db_fingerprint=\"{}\" status_filter={} page={} size={} sort={} filters={} total_elements={} returned_ids={}",
            dbFingerprint(),
            LoadStatus.PUBLISHED,
            page,
            size,
            sortAsc ? "createdAt:asc" : "createdAt:desc",
            filters,
            result.getTotalElements(),
            items.stream().map(load -> load.id().toString()).toList()
        );

        return new LoadPageResult(
            items,
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
        PersistedLoadPublication saved = persisted.toDomain();

        LOGGER.info(
            "LOAD_UPDATE_RESULT table=loadix.loads db_fingerprint=\"{}\" id={} warehouse_user_id={} status={} updated_at={}",
            dbFingerprint(),
            saved.id(),
            saved.warehouseUserId(),
            saved.status(),
            saved.updatedAt()
        );

        return saved;
    }

    @Override
    public Optional<PersistedLoadPublication> findById(UUID loadId) {
        return loadJpaRepository.findById(loadId).map(LoadJpaEntity::toDomain);
    }

    @Override
    public long countCreatedByWarehouseUserIdBetween(UUID warehouseUserId, Instant fromInclusive, Instant toExclusive) {
        return loadJpaRepository.countByWarehouseUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            warehouseUserId,
            fromInclusive,
            toExclusive
        );
    }

    @Override
    public List<PersistedLoadPublication> findCreatedByWarehouseUserIdBetween(
        UUID warehouseUserId,
        Instant fromInclusive,
        Instant toExclusive
    ) {
        return loadJpaRepository.findByWarehouseUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            warehouseUserId,
            fromInclusive,
            toExclusive
        ).stream().map(LoadJpaEntity::toDomain).toList();
    }

    @Override
    public List<LoadStatusCount> countByWarehouseUserIdGroupedByStatus(UUID warehouseUserId) {
        return loadJpaRepository.countByWarehouseUserIdGroupedByStatus(warehouseUserId).stream()
            .map(row -> new LoadStatusCount(LoadStatus.valueOf(row.getStatus()), row.getTotal()))
            .toList();
    }

    @Override
    public long countByStatus(LoadStatus status) {
        return loadJpaRepository.countByStatus(status.name());
    }

    @Override
    public List<PersistedLoadPublication> findByStatusAndCreatedAtBetween(
        LoadStatus status,
        Instant fromInclusive,
        Instant toExclusive
    ) {
        return loadJpaRepository.findByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            status.name(),
            fromInclusive,
            toExclusive
        ).stream().map(LoadJpaEntity::toDomain).toList();
    }

    @Override
    public List<CargoTypeCount> countByStatusGroupedByCargoType(LoadStatus status) {
        return loadJpaRepository.countByStatusGroupedByCargoType(status.name()).stream()
            .map(row -> new CargoTypeCount(CargoType.valueOf(row.getCargoType()), row.getTotal()))
            .toList();
    }

    private Specification<LoadJpaEntity> byWarehouseUserId(UUID warehouseUserId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("warehouseUserId"), warehouseUserId);
    }

    private String dbFingerprint() {
        return jdbcTemplate.queryForObject(
            "select concat('database=', current_database(), '; schema=', current_schema(), '; user=', current_user, '; server=', coalesce(inet_server_addr()::text, 'unknown'), ':', coalesce(inet_server_port()::text, 'unknown'))",
            String.class
        );
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

    private Specification<LoadJpaEntity> byStatus(LoadStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status.name());
    }

    private Specification<LoadJpaEntity> byQuery(String queryValue) {
        if (isBlank(queryValue)) {
            return null;
        }

        String pattern = likePattern(queryValue);
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
            criteriaBuilder.like(criteriaBuilder.lower(root.get("originCity")), pattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("originAddress")), pattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("originPostalCode")), pattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("destinationCity")), pattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("destinationAddress")), pattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("destinationPostalCode")), pattern)
        );
    }

    private Specification<LoadJpaEntity> byOrigin(String origin) {
        if (isBlank(origin)) {
            return null;
        }

        String pattern = likePattern(origin);
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
            criteriaBuilder.like(criteriaBuilder.lower(root.get("originCity")), pattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("originAddress")), pattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("originPostalCode")), pattern)
        );
    }

    private Specification<LoadJpaEntity> byDestination(String destination) {
        if (isBlank(destination)) {
            return null;
        }

        String pattern = likePattern(destination);
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
            criteriaBuilder.like(criteriaBuilder.lower(root.get("destinationCity")), pattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("destinationAddress")), pattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("destinationPostalCode")), pattern)
        );
    }

    private Specification<LoadJpaEntity> byPickupDate(LocalDate pickupDate) {
        if (pickupDate == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pickupDate"), pickupDate);
    }

    private Specification<LoadJpaEntity> byPalletsMin(Integer palletsMin) {
        if (palletsMin == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("loadQuantity"), palletsMin);
    }

    private Specification<LoadJpaEntity> byPalletsMax(Integer palletsMax) {
        if (palletsMax == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("loadQuantity"), palletsMax);
    }

    private Specification<LoadJpaEntity> byWeightKgMin(BigDecimal weightKgMin) {
        if (weightKgMin == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("weightKg"), weightKgMin);
    }

    private Specification<LoadJpaEntity> byWeightKgMax(BigDecimal weightKgMax) {
        if (weightKgMax == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("weightKg"), weightKgMax);
    }

    private Specification<LoadJpaEntity> byCargoType(CargoType cargoType) {
        if (cargoType == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("cargoType"), cargoType.name());
    }

    private Specification<LoadJpaEntity> byPriceMin(BigDecimal priceMin) {
        if (priceMin == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("basePriceAmount"), priceMin);
    }

    private Specification<LoadJpaEntity> byPriceMax(BigDecimal priceMax) {
        if (priceMax == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("basePriceAmount"), priceMax);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String likePattern(String value) {
        return "%" + value.trim().toLowerCase() + "%";
    }
}
