package com.loadix.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.loadix.domain.valueobject.CargoType;

public record AvailableLoadsFilters(
    String query,
    String origin,
    String destination,
    LocalDate pickupDate,
    Integer palletsMin,
    Integer palletsMax,
    BigDecimal weightKgMin,
    BigDecimal weightKgMax,
    CargoType cargoType,
    BigDecimal priceMin,
    BigDecimal priceMax
) {
}
