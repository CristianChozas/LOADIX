package com.loadix.application.dto.request;

import com.loadix.domain.valueobject.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CreateCarrierProfileRequest", description = "Payload used to create a carrier profile")
public record CreateCarrierProfileRequest (
    @Schema(description = "First name", example = "Carlos")
    @NotBlank String name,
    @Schema(description = "Last name", example = "Gomez")
    @NotBlank String lastName,
    @Schema(description = "Phone number", example = "612345678")
    @NotBlank String phone,
    @Schema(description = "Vehicle type", example = "FURGONETA")
    @NotNull VehicleType vehicleType,
    @Schema(description = "License plate", example = "1234ABC")
    @NotBlank String licensePlate,
    @Schema(description = "Professional carnet", example = "C1")
    @NotBlank String carnet
    ) {
}
