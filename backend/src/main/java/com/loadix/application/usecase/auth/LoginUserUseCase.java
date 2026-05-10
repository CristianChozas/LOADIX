package com.loadix.application.usecase.auth;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.loadix.application.dto.request.LoginRequest;
import com.loadix.application.dto.response.AuthSessionResponse;
import com.loadix.application.mapper.AuthMapper;
import com.loadix.application.port.in.LoginUserPort;
import com.loadix.application.port.out.AuthTokenPort;
import com.loadix.application.port.out.PasswordHasherPort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.InvalidCredentialsException;
import com.loadix.domain.model.UserAccount;

public class LoginUserUseCase implements LoginUserPort {

    private final UserAccountPort userAccountPort;
    private final PasswordHasherPort passwordHasherPort;
    private final AuthTokenPort authTokenPort;
    private final AuthMapper authMapper;

    public LoginUserUseCase(
            UserAccountPort userAccountPort,
            PasswordHasherPort passwordHasherPort,
            AuthTokenPort authTokenPort,
            AuthMapper authMapper
    ) {
        this.userAccountPort = Objects.requireNonNull(userAccountPort, "userAccountPort cannot be null");
        this.passwordHasherPort = Objects.requireNonNull(passwordHasherPort, "passwordHasherPort cannot be null");
        this.authTokenPort = Objects.requireNonNull(authTokenPort, "authTokenPort cannot be null");
        this.authMapper = Objects.requireNonNull(authMapper, "authMapper cannot be null");
    }

    @Override
    public AuthSessionResponse execute(LoginRequest request) {
        LoginRequest nonNullRequest = Objects.requireNonNull(request, "request cannot be null");
        String email = normalizeEmail(nonNullRequest.email());

        UserAccount user = userAccountPort.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHasherPort.matches(nonNullRequest.password(), user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = authTokenPort.createToken(user.email(), List.of(user.role().name()));

        return new AuthSessionResponse(
                authMapper.toAuthUserResponse(user),
                token
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
