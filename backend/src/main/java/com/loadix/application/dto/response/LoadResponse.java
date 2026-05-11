package com.loadix.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.loadix.domain.valueobject.CargoType;
import com.loadix.domain.valueobject.LoadStatus;
import com.loadix.domain.valueobject.LoadUnitType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoadResponse", description = "Published load payload")
public record LoadResponse(
    @Schema(description = "Load identifier")
    UUID id,
    @Schema(description = "Warehouse owner user identifier")
    UUID warehouseUserId,
    @Schema(description = "Owner user identifier")
    UUID createdByUserId,
    @Schema(description = "Origin location")
    LocationResponse origin,
    @Schema(description = "Destination location")
    LocationResponse destination,
    @Schema(description = "Cargo type")
    CargoType cargoType,
    @Schema(description = "Cargo weight in kilograms")
    BigDecimal weightKg,
    @Schema(description = "Load quantity")
    int loadQuantity,
    @Schema(description = "Load quantity unit")
    LoadUnitType loadUnitType,
    @Schema(description = "Pickup date")
    LocalDate pickupDate,
    @Schema(description = "Base price amount without VAT")
    BigDecimal basePriceAmount,
    @Schema(description = "Optional operational notes")
    String notes,
    @Schema(description = "Optional special requirements")
    String specialRequirements,
    @Schema(description = "Load status")
    LoadStatus status,
    @Schema(description = "Creation timestamp")
    Instant createdAt,
    @Schema(description = "Last update timestamp")
    Instant updatedAt
) {
    @Schema(name = "LoadLocationResponse", description = "Structured location payload")
    public record LocationResponse(
        String address,
        String city,
        String postalCode
    ) {
    }
}
