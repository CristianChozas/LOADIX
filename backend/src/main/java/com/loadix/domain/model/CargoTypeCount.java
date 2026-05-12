package com.loadix.domain.model;

import com.loadix.domain.valueobject.CargoType;

public record CargoTypeCount(
    CargoType cargoType,
    long count
) {
}
