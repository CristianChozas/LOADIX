package com.loadix.application.usecase.auth;

import java.util.Locale;
import java.util.Objects;

import com.loadix.application.dto.request.RegisterRequest;
import com.loadix.application.dto.response.AuthUserResponse;
import com.loadix.application.mapper.AuthMapper;
import com.loadix.application.port.in.RegisterUserPort;
import com.loadix.application.port.out.PasswordHasherPort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.UserAlreadyExistsException;
import com.loadix.domain.model.UserAccount;

public class RegisterUserUseCase implements RegisterUserPort {

    private final UserAccountPort userAccountPort;
    private final PasswordHasherPort passwordHasherPort;
    private final AuthMapper authMapper;

    public RegisterUserUseCase(
            UserAccountPort userAccountPort,
            PasswordHasherPort passwordHasherPort,
            AuthMapper authMapper) {
        this.userAccountPort = Objects.requireNonNull(userAccountPort,
                "userAccountPort cannot be null");
        this.passwordHasherPort = Objects.requireNonNull(passwordHasherPort, "passwordHasherPort cannot be null");
        this.authMapper = Objects.requireNonNull(authMapper, "authMapper cannot be null");
    }

    @Override
    public AuthUserResponse execute(RegisterRequest request) {
        RegisterRequest nonNullRequest = Objects.requireNonNull(request, "request cannot be null");
        String email = normalizeEmail(nonNullRequest.email());

        if (userAccountPort.existsByEmail(email)) {
            throw new UserAlreadyExistsException();
        }

        UserAccount saved = userAccountPort.save(new UserAccount(
                null,
                email,
                passwordHasherPort.hash(nonNullRequest.password()),
                nonNullRequest.role(),
                false));

        return authMapper.toAuthUserResponse(saved);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
