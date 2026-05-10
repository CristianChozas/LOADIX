package com.loadix.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.loadix.domain.model.CarrierProfile;

public interface CarrierProfilePort {

    CarrierProfile save(CarrierProfile profile);

    Optional<CarrierProfile> findByUserId(UUID userId);
    
}
