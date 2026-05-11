package com.loadix.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.loadix.infrastructure.persistence.entity.LoadJpaEntity;

public interface LoadJpaRepository extends JpaRepository<LoadJpaEntity, UUID>, JpaSpecificationExecutor<LoadJpaEntity> {
}
