package com.loadix.application.usecase;

import java.util.Locale;

import com.loadix.application.dto.response.AuthUserResponse;
import com.loadix.application.exception.ApplicationAuthenticationException;
import com.loadix.application.port.out.UserAccountRepository;

public class GetCurrentUserUseCase {

    private final UserAccountRepository userAccountRepository;

    public GetCurrentUserUseCase(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public AuthUserResponse getByEmail(String email) {
        var user = userAccountRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new ApplicationAuthenticationException("Authenticated user not found"));

        return new AuthUserResponse(
                user.id(),
                user.email(),
                user.role(),
                user.profileCompleted()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
