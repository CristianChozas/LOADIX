package com.loadix.infrastructure.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.loadix.application.port.out.UserAccountRepository;
import com.loadix.domain.model.UserAccount;
import com.loadix.infrastructure.persistence.entity.UserJpaEntity;
import com.loadix.infrastructure.persistence.repository.UserJpaRepository;

@Component
public class UserPersistenceAdapter implements UserAccountRepository {

    private final UserJpaRepository userJpaRepository;

    public UserPersistenceAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return userJpaRepository.findByEmailIgnoreCase(email).map(UserJpaEntity::toDomain);
    }

    @Override
    public UserAccount save(UserAccount account) {
        UserJpaEntity persisted = userJpaRepository.save(UserJpaEntity.fromDomain(account));
        return persisted.toDomain();
    }
}
