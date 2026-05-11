package com.loadix.application.port.out;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import com.loadix.domain.model.AvailableLoadsFilters;
import com.loadix.domain.model.LoadPublication;
import com.loadix.domain.model.LoadPageResult;
import com.loadix.domain.model.PersistedLoadPublication;

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
}
