package com.loadix.domain.model;

import java.util.UUID;

import com.loadix.domain.valueobject.VehicleType;

public record CarrierProfile(
        UUID id,
        String name,
        String lastName,
        String phone,
        VehicleType vehicleType,
        String licensePlate,
        String carnet

) {
    public boolean isCompleted() {
        return name != null && !name.isBlank()
                && lastName != null && !lastName.isBlank()
                && phone != null && !phone.isBlank()
                && vehicleType != null
                && licensePlate != null && !licensePlate.isBlank()
                && carnet != null && !carnet.isBlank();
    }
}
