package com.loadix.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.loadix.domain.model.LoadPublication;
import com.loadix.domain.model.PersistedLoadPublication;
import com.loadix.domain.valueobject.CargoType;
import com.loadix.domain.valueobject.LoadStatus;
import com.loadix.domain.valueobject.LoadUnitType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "loads")
public class LoadJpaEntity extends BaseJpaEntity {

    @Column(name = "warehouse_user_id", nullable = false)
    private UUID warehouseUserId;

    @Column(name = "created_by_user_id", nullable = false)
    private UUID createdByUserId;

    @Column(name = "origin_address", nullable = false, length = 255)
    private String originAddress;

    @Column(name = "origin_city", nullable = false, length = 100)
    private String originCity;

    @Column(name = "origin_postal_code", nullable = false, length = 20)
    private String originPostalCode;

    @Column(name = "destination_address", nullable = false, length = 255)
    private String destinationAddress;

    @Column(name = "destination_city", nullable = false, length = 100)
    private String destinationCity;

    @Column(name = "destination_postal_code", nullable = false, length = 20)
    private String destinationPostalCode;

    @Column(name = "cargo_type", nullable = false, length = 50)
    private String cargoType;

    @Column(name = "weight_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "load_quantity", nullable = false)
    private int loadQuantity;

    @Column(name = "load_unit_type", nullable = false, length = 32)
    private String loadUnitType;

    @Column(name = "pickup_date", nullable = false)
    private LocalDate pickupDate;

    @Column(name = "base_price_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePriceAmount;

    @Column(name = "notes")
    private String notes;

    @Column(name = "special_requirements")
    private String specialRequirements;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    protected LoadJpaEntity() {
    }

    public static LoadJpaEntity fromDomain(LoadPublication loadPublication) {
        LoadJpaEntity entity = new LoadJpaEntity();
        entity.warehouseUserId = loadPublication.warehouseUserId();
        entity.createdByUserId = loadPublication.createdByUserId();
        entity.originAddress = loadPublication.originAddress();
        entity.originCity = loadPublication.originCity();
        entity.originPostalCode = loadPublication.originPostalCode();
        entity.destinationAddress = loadPublication.destinationAddress();
        entity.destinationCity = loadPublication.destinationCity();
        entity.destinationPostalCode = loadPublication.destinationPostalCode();
        entity.cargoType = loadPublication.cargoType().name();
        entity.weightKg = loadPublication.weightKg();
        entity.loadQuantity = loadPublication.loadQuantity();
        entity.loadUnitType = loadPublication.loadUnitType().name();
        entity.pickupDate = loadPublication.pickupDate();
        entity.basePriceAmount = loadPublication.basePriceAmount();
        entity.notes = loadPublication.notes();
        entity.specialRequirements = loadPublication.specialRequirements();
        entity.status = loadPublication.status().name();
        return entity;
    }

    public PersistedLoadPublication toDomain() {
        return new PersistedLoadPublication(
            getId(),
            warehouseUserId,
            createdByUserId,
            originAddress,
            originCity,
            originPostalCode,
            destinationAddress,
            destinationCity,
            destinationPostalCode,
            CargoType.valueOf(cargoType),
            weightKg,
            loadQuantity,
            LoadUnitType.valueOf(loadUnitType),
            pickupDate,
            basePriceAmount,
            notes,
            specialRequirements,
            LoadStatus.valueOf(status),
            getCreatedAt(),
            getUpdatedAt()
        );
    }

    public void updateFromDomain(LoadPublication loadPublication) {
        this.originAddress = loadPublication.originAddress();
        this.originCity = loadPublication.originCity();
        this.originPostalCode = loadPublication.originPostalCode();
        this.destinationAddress = loadPublication.destinationAddress();
        this.destinationCity = loadPublication.destinationCity();
        this.destinationPostalCode = loadPublication.destinationPostalCode();
        this.cargoType = loadPublication.cargoType().name();
        this.weightKg = loadPublication.weightKg();
        this.loadQuantity = loadPublication.loadQuantity();
        this.loadUnitType = loadPublication.loadUnitType().name();
        this.pickupDate = loadPublication.pickupDate();
        this.basePriceAmount = loadPublication.basePriceAmount();
        this.notes = loadPublication.notes();
        this.specialRequirements = loadPublication.specialRequirements();
        this.status = loadPublication.status().name();
    }

    public UUID getWarehouseUserId() {
        return warehouseUserId;
    }

    public UUID getCreatedByUserId() {
        return createdByUserId;
    }
}
