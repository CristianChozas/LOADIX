package com.loadix.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.loadix.domain.model.WarehouseProfile;

public interface WarehouseProfilePort {

    WarehouseProfile save(WarehouseProfile profile);

    Optional<WarehouseProfile> findByUserId(UUID id);

}
