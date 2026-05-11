package com.loadix.infrastructure.persistence.adapter;

import org.springframework.stereotype.Component;

import com.loadix.application.port.out.LoadPort;
import com.loadix.domain.model.LoadPublication;
import com.loadix.domain.model.PersistedLoadPublication;
import com.loadix.infrastructure.persistence.entity.LoadJpaEntity;
import com.loadix.infrastructure.persistence.repository.LoadJpaRepository;

@Component
public class LoadPersistenceAdapter implements LoadPort {

    private final LoadJpaRepository loadJpaRepository;

    public LoadPersistenceAdapter(LoadJpaRepository loadJpaRepository) {
        this.loadJpaRepository = loadJpaRepository;
    }

    @Override
    public PersistedLoadPublication save(LoadPublication loadPublication) {
        LoadJpaEntity entity = LoadJpaEntity.fromDomain(loadPublication);
        LoadJpaEntity persisted = loadJpaRepository.save(entity);
        return persisted.toDomain();
    }
}
