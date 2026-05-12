package com.loadix.application.port.out;

import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.loadix.domain.model.AvailableLoadsFilters;
import com.loadix.domain.model.CargoTypeCount;
import com.loadix.domain.model.LoadStatusCount;
import com.loadix.domain.model.LoadPublication;
import com.loadix.domain.model.LoadPageResult;
import com.loadix.domain.model.PersistedLoadPublication;
import com.loadix.domain.valueobject.LoadStatus;

public interface LoadPort {

    PersistedLoadPublication save(LoadPublication loadPublication);

    LoadPageResult findByWarehouseUserId(
        UUID warehouseUserId,
        int page,
        int size,
        boolean sortAsc,
        LocalDate pickupDateFrom,
        LocalDate pickupDateTo
    );

    LoadPageResult findAvailableLoads(
        int page,
        int size,
        boolean sortAsc,
        AvailableLoadsFilters filters
    );

    PersistedLoadPublication updateById(UUID loadId, LoadPublication loadPublication);

    Optional<PersistedLoadPublication> findById(UUID loadId);

    long countCreatedByWarehouseUserIdBetween(UUID warehouseUserId, Instant fromInclusive, Instant toExclusive);

    List<PersistedLoadPublication> findCreatedByWarehouseUserIdBetween(UUID warehouseUserId, Instant fromInclusive, Instant toExclusive);

    List<LoadStatusCount> countByWarehouseUserIdGroupedByStatus(UUID warehouseUserId);

    long countByStatus(LoadStatus status);

    List<PersistedLoadPublication> findByStatusAndCreatedAtBetween(LoadStatus status, Instant fromInclusive, Instant toExclusive);

    List<CargoTypeCount> countByStatusGroupedByCargoType(LoadStatus status);
}
