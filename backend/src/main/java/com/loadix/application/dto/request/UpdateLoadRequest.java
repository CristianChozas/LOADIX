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

@Schema(name = "UpdateLoadRequest", description = "Payload used to update a published load")
public record UpdateLoadRequest(
    @Valid
    @NotNull
    LocationRequest origin,
    @Valid
    @NotNull
    LocationRequest destination,
    @NotNull
    CargoType cargoType,
    @NotNull
    @DecimalMin(value = "0.01")
    BigDecimal weightKg,
    @NotNull
    @FutureOrPresent
    LocalDate pickupDate,
    @NotNull
    @DecimalMin(value = "0.01")
    BigDecimal basePriceAmount,
    String notes,
    String specialRequirements
) {
    public record LocationRequest(
        @NotBlank String address,
        @NotBlank String city,
        @NotBlank String postalCode
    ) {
    }
}
