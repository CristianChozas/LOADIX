package com.loadix.application.usecase.auth;

import java.util.Locale;
import java.util.Objects;

import com.loadix.application.dto.request.RegisterRequest;
import com.loadix.application.dto.response.AuthUserResponse;
import com.loadix.application.mapper.AuthMapper;
import com.loadix.application.port.in.RegisterUserInputPort;
import com.loadix.application.port.out.PasswordHasher;
import com.loadix.application.port.out.UserAccountRepository;
import com.loadix.domain.exception.UserAlreadyExistsException;
import com.loadix.domain.model.UserAccount;

public class RegisterUserUseCase implements RegisterUserInputPort {

    private final UserAccountRepository userAccountRepository;
    private final PasswordHasher passwordHasher;
    private final AuthMapper authMapper;

    public RegisterUserUseCase(
            UserAccountRepository userAccountRepository,
            PasswordHasher passwordHasher,
            AuthMapper authMapper) {
        this.userAccountRepository = Objects.requireNonNull(userAccountRepository, "userAccountRepository cannot be null");
        this.passwordHasher = Objects.requireNonNull(passwordHasher, "passwordHasher cannot be null");
        this.authMapper = Objects.requireNonNull(authMapper, "authMapper cannot be null");
    }

    @Override
    public AuthUserResponse execute(RegisterRequest request) {
        RegisterRequest nonNullRequest = Objects.requireNonNull(request, "request cannot be null");
        String email = normalizeEmail(nonNullRequest.email());

        if (userAccountRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException();
        }

        UserAccount saved = userAccountRepository.save(new UserAccount(
                null,
                email,
                passwordHasher.hash(nonNullRequest.password()),
                nonNullRequest.role(),
                false));

        return authMapper.toAuthUserResponse(saved);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
