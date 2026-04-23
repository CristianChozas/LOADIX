package com.loadix.application.dto.request;

import com.loadix.domain.valueobject.CargoType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UpdateWarehouseProfileRequest", description = "Payload used to update a warehouse profile")
public record UpdateWarehouseProfileRequest(
    @Schema(description = "Company name", example = "Almacenes Norte")
    String companyName,
    @Schema(description = "Tax ID", example = "B12345678")
    String cif,
    @Schema(description = "Street address", example = "Calle Logistica 1")
    String address,
    @Schema(description = "Postal code", example = "28001")
    String postalCode,
    @Schema(description = "City", example = "Madrid")
    String city,
    @Schema(description = "Phone number", example = "600000000")
    String phone,
    @Schema(description = "Main contact person", example = "Ana Lopez")
    String contactPerson,
    @Schema(description = "Cargo type", example = "PALLETIZED")
    CargoType cargoType
    ) {
}
