package com.loadix.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.loadix.domain.valueobject.CargoType;
import com.loadix.domain.valueobject.LoadStatus;
import com.loadix.domain.valueobject.LoadUnitType;

public record LoadPublication(
    UUID warehouseUserId,
    UUID createdByUserId,
    String originAddress,
    String originCity,
    String originPostalCode,
    String destinationAddress,
    String destinationCity,
    String destinationPostalCode,
    CargoType cargoType,
    BigDecimal weightKg,
    int loadQuantity,
    LoadUnitType loadUnitType,
    LocalDate pickupDate,
    BigDecimal basePriceAmount,
    String notes,
    String specialRequirements,
    LoadStatus status
) {
}
