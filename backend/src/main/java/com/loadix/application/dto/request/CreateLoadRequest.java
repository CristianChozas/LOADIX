package com.loadix.application.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.loadix.domain.valueobject.CargoType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CreateLoadRequest", description = "Payload used to publish a new load")
public record CreateLoadRequest(
    @Valid
    @NotNull
    @Schema(description = "Origin location")
    LocationRequest origin,
    @Valid
    @NotNull
    @Schema(description = "Destination location")
    LocationRequest destination,
    @NotNull
    @Schema(description = "Cargo type", example = "PALLETIZED")
    CargoType cargoType,
    @NotNull
    @DecimalMin(value = "0.01")
    @Schema(description = "Cargo weight in kilograms", example = "1200.50")
    BigDecimal weightKg,
    @NotNull
    @FutureOrPresent
    @Schema(description = "Pickup date", example = "2026-06-01")
    LocalDate pickupDate,
    @NotNull
    @DecimalMin(value = "0.01")
    @Schema(description = "Base price amount without VAT", example = "850.00")
    BigDecimal basePriceAmount,
    @Schema(description = "Optional operational notes")
    String notes,
    @Schema(description = "Optional special requirements")
    String specialRequirements
) {
    @Schema(name = "CreateLoadLocationRequest", description = "Structured location for origin or destination")
    public record LocationRequest(
        @NotBlank
        @Schema(description = "Street address", example = "Calle Logística 12")
        String address,
        @NotBlank
        @Schema(description = "City", example = "Madrid")
        String city,
        @NotBlank
        @Schema(description = "Postal code", example = "28001")
        String postalCode
    ) {
    }
}
