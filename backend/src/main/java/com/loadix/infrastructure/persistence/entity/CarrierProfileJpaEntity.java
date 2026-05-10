package com.loadix.infrastructure.persistence.entity;

import java.util.UUID;

import com.loadix.domain.model.CarrierProfile;
import com.loadix.domain.valueobject.VehicleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "carrier_profiles")
public class CarrierProfileJpaEntity extends BaseJpaEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 32)
    private VehicleType vehicleType;

    @Column(name = "license_place", nullable = false, length = 20)
    private String licensePlate;

    @Column(nullable = false, length = 20)
    private String carnet;

    protected CarrierProfileJpaEntity() {
    }

    private CarrierProfileJpaEntity(String name, String lastName, String phone, VehicleType vehicleType,
            String licensePlate, String carnet) {
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.carnet = carnet;
    }

    public static CarrierProfileJpaEntity fromDomain(CarrierProfile profile) {
        CarrierProfileJpaEntity entity = new CarrierProfileJpaEntity(
                profile.name(),
                profile.lastName(),
                profile.phone(),
                profile.vehicleType(),
                profile.licensePlate(),
                profile.carnet());
        entity.userId = profile.id();
        return entity;
    }

    public void reusePersistentId(UUID id) {
        setId(id);
    }

    public CarrierProfile toDomain() {
        return new CarrierProfile(
                userId,
                name,
                lastName,
                phone,
                vehicleType,
                licensePlate,
                carnet);
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getCarnet() {
        return carnet;
    }

    public UUID getUserId() {
        return userId;
    }
}
