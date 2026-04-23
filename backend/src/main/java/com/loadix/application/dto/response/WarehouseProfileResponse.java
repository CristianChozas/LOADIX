package com.loadix.application.dto.response;

import com.loadix.domain.valueobject.CargoType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "WarehouseProfileResponse", description = "Warehouse profile payload")
public record WarehouseProfileResponse(
    @Schema(description = "Company name")
    String companyName,
    @Schema(description = "Tax ID")
    String cif,
    @Schema(description = "Street address")
    String address,
    @Schema(description = "Postal code")
    String postalCode,
    @Schema(description = "City")
    String city,
    @Schema(description = "Phone number")
    String phone,
    @Schema(description = "Main contact person")
    String contactPerson,
    @Schema(description = "Cargo type")
    CargoType cargoType,
    @Schema(description = "Whether the profile is complete")
    boolean completed
) {
}
