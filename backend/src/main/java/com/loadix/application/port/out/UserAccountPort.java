package com.loadix.application.port.out;

import java.util.Optional;

import com.loadix.domain.model.UserAccount;

public interface UserAccountPort {

    boolean existsByEmail(String email);

    Optional<UserAccount> findByEmail(String email);

    UserAccount save(UserAccount account);

    UserAccount updateEmail(String currentEmail, String newEmail);
}
