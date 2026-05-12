package com.loadix.infrastructure.persistence.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.loadix.infrastructure.persistence.entity.LoadJpaEntity;
import com.loadix.infrastructure.persistence.repository.projection.CargoTypeCountProjection;
import com.loadix.infrastructure.persistence.repository.projection.LoadStatusCountProjection;

public interface LoadJpaRepository extends JpaRepository<LoadJpaEntity, UUID>, JpaSpecificationExecutor<LoadJpaEntity> {

    long countByWarehouseUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
        UUID warehouseUserId,
        Instant fromInclusive,
        Instant toExclusive
    );

    List<LoadJpaEntity> findByWarehouseUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
        UUID warehouseUserId,
        Instant fromInclusive,
        Instant toExclusive
    );

    long countByStatus(String status);

    List<LoadJpaEntity> findByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
        String status,
        Instant fromInclusive,
        Instant toExclusive
    );

    @Query("""
        select l.status as status, count(l) as total
        from LoadJpaEntity l
        where l.warehouseUserId = :warehouseUserId
        group by l.status
    """)
    List<LoadStatusCountProjection> countByWarehouseUserIdGroupedByStatus(@Param("warehouseUserId") UUID warehouseUserId);

    @Query("""
        select l.cargoType as cargoType, count(l) as total
        from LoadJpaEntity l
        where l.status = :status
        group by l.cargoType
    """)
    List<CargoTypeCountProjection> countByStatusGroupedByCargoType(@Param("status") String status);
}
