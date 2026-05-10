package com.loadix.application.dto.request;

import com.loadix.domain.valueobject.VehicleType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UpdateCarrierProfileRequest", description = "Payload used to update a carrier profile")
public record UpdateCarrierProfileRequest(
    @Schema(description = "First name", example = "Carlos")
    String name,
    @Schema(description = "Last name", example = "Gomez")
    String lastName,
    @Schema(description = "Phone number", example = "612345678")
    String phone,
    @Schema(description = "Vehicle type", example = "FURGONETA")
    VehicleType vehicleType,
    @Schema(description = "License plate", example = "1234ABC")
    String licensePlate,
    @Schema(description = "Professional carnet", example = "C1")
    String carnet
) {
}
