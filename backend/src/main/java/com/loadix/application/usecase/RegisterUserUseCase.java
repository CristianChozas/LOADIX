package com.loadix.application.usecase;

import java.util.Locale;

import com.loadix.application.dto.request.RegisterRequest;
import com.loadix.application.dto.response.AuthUserResponse;
import com.loadix.application.exception.ApplicationConflictException;
import com.loadix.application.port.out.PasswordHasher;
import com.loadix.application.port.out.UserAccountRepository;
import com.loadix.domain.auth.UserAccount;

public class RegisterUserUseCase {

    private final UserAccountRepository userAccountRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserUseCase(
            UserAccountRepository userAccountRepository,
            PasswordHasher passwordHasher) {
        this.userAccountRepository = userAccountRepository;
        this.passwordHasher = passwordHasher;
    }

    public AuthUserResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());

        if (userAccountRepository.existsByEmail(email)) {
            throw new ApplicationConflictException("Email already exists");
        }

        UserAccount saved = userAccountRepository.save(new UserAccount(
                null,
                email,
                passwordHasher.hash(request.password()),
                request.role(),
                false));

        return new AuthUserResponse(
                saved.id(),
                saved.email(),
                saved.role(),
                saved.profileCompleted());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
