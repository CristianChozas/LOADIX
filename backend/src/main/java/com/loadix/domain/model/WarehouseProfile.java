package com.loadix.domain.model;

import java.util.UUID;

import com.loadix.domain.valueobject.CargoType;

public record WarehouseProfile(
    UUID userId,
    String companyName,
    String cif,
    String address,
    String postalCode,
    String city,
    String phone,
    String contactPerson,
    CargoType cargoType
) {

    public boolean isCompleted() {
        return companyName != null && !companyName.isBlank() &&
            cif != null && !cif.isBlank() &&
            address != null && !address.isBlank() &&
            postalCode != null && !postalCode.isBlank() &&
            city != null && !city.isBlank() &&
            phone != null && !phone.isBlank() &&
            contactPerson != null && !contactPerson.isBlank() &&
            cargoType != null;
    }
}