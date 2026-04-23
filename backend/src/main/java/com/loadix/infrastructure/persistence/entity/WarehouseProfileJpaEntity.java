package com.loadix.infrastructure.persistence.entity;

import java.util.UUID;

import com.loadix.domain.model.WarehouseProfile;
import com.loadix.domain.valueobject.CargoType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "warehouse_profiles")
public class WarehouseProfileJpaEntity extends BaseJpaEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "cif", length = 50)
    private String cif;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "cargo_type", length = 50)
    private String cargoType;

    protected WarehouseProfileJpaEntity() {
    }

    public static WarehouseProfileJpaEntity fromDomain(WarehouseProfile profile) {
        WarehouseProfileJpaEntity entity = new WarehouseProfileJpaEntity();
        entity.userId = profile.userId();
        entity.companyName = profile.companyName();
        entity.cif = profile.cif();
        entity.address = profile.address();
        entity.postalCode = profile.postalCode();
        entity.city = profile.city();
        entity.phone = profile.phone();
        entity.contactPerson = profile.contactPerson();
        entity.cargoType = profile.cargoType() != null ? profile.cargoType().name() : null;
        return entity;
    }

    public void reusePersistentId(UUID id) {
        setId(id);
    }

    public WarehouseProfile toDomain() {
        return new WarehouseProfile(
                userId,
                companyName,
                cif,
                address,
                postalCode,
                city,
                phone,
                contactPerson,
                cargoType != null ? CargoType.valueOf(cargoType) : null
        );
    }

    public UUID getUserId() {
        return userId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCif() {
        return cif;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getCargoType() {
        return cargoType;
    }
}
