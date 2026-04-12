package com.loadix.application.usecase.auth;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.loadix.application.dto.request.LoginRequest;
import com.loadix.application.dto.response.AuthSessionResponse;
import com.loadix.application.mapper.AuthMapper;
import com.loadix.application.port.in.LoginUserInputPort;
import com.loadix.application.port.out.AuthTokenService;
import com.loadix.application.port.out.PasswordHasher;
import com.loadix.application.port.out.UserAccountRepository;
import com.loadix.domain.exception.InvalidCredentialsException;
import com.loadix.domain.model.UserAccount;

public class LoginUserUseCase implements LoginUserInputPort {

    private final UserAccountRepository userAccountRepository;
    private final PasswordHasher passwordHasher;
    private final AuthTokenService authTokenService;
    private final AuthMapper authMapper;

    public LoginUserUseCase(
            UserAccountRepository userAccountRepository,
            PasswordHasher passwordHasher,
            AuthTokenService authTokenService,
            AuthMapper authMapper
    ) {
        this.userAccountRepository = Objects.requireNonNull(userAccountRepository, "userAccountRepository cannot be null");
        this.passwordHasher = Objects.requireNonNull(passwordHasher, "passwordHasher cannot be null");
        this.authTokenService = Objects.requireNonNull(authTokenService, "authTokenService cannot be null");
        this.authMapper = Objects.requireNonNull(authMapper, "authMapper cannot be null");
    }

    @Override
    public AuthSessionResponse execute(LoginRequest request) {
        LoginRequest nonNullRequest = Objects.requireNonNull(request, "request cannot be null");
        String email = normalizeEmail(nonNullRequest.email());

        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHasher.matches(nonNullRequest.password(), user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = authTokenService.createToken(user.email(), List.of(user.role().name()));

        return new AuthSessionResponse(
                authMapper.toAuthUserResponse(user),
                token
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
