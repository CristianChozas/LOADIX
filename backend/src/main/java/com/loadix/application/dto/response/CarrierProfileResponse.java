package com.loadix.application.dto.response;

import com.loadix.domain.valueobject.VehicleType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CarrierProfileResponse", description = "Carrier profile payload")
public record CarrierProfileResponse (
    @Schema(description = "First name")
    String name, 
    @Schema(description = "Last name")
    String lastName,
    @Schema(description = "Phone number")
    String phone,
    @Schema(description = "Vehicle type")
    VehicleType vehicleType,
    @Schema(description = "License plate")
    String licensePlate,
    @Schema(description = "Professional carnet")
    String carnet,
    @Schema(description = "Whether the profile is complete")
    boolean completed

) {
}
