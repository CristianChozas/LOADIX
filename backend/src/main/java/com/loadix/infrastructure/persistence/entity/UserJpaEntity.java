package com.loadix.infrastructure.persistence.entity;

import com.loadix.domain.model.UserAccount;
import com.loadix.domain.valueobject.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserJpaEntity extends BaseJpaEntity {

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UserRole role;

    @Column(name = "profile_completed", nullable = false)
    private boolean profileCompleted;

    protected UserJpaEntity() {
    }

    private UserJpaEntity(String email, String passwordHash, UserRole role, boolean profileCompleted) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.profileCompleted = profileCompleted;
    }

    public static UserJpaEntity fromDomain(UserAccount account) {
        return new UserJpaEntity(account.email(), account.passwordHash(), account.role(), account.profileCompleted());
    }

    public UserAccount toDomain() {
        return new UserAccount(getId(), email, passwordHash, role, profileCompleted);
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }
}
