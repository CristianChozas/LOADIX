package com.loadix.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loadix.infrastructure.persistence.entity.UserJpaEntity;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<UserJpaEntity> findByEmailIgnoreCase(String email);
}
