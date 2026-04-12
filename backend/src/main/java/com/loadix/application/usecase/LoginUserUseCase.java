package com.loadix.application.usecase;

import java.util.List;
import java.util.Locale;

import com.loadix.application.dto.request.LoginRequest;
import com.loadix.application.dto.response.AuthSessionResponse;
import com.loadix.application.dto.response.AuthUserResponse;
import com.loadix.application.exception.ApplicationAuthenticationException;
import com.loadix.application.port.out.AuthTokenService;
import com.loadix.application.port.out.PasswordHasher;
import com.loadix.application.port.out.UserAccountRepository;
import com.loadix.domain.auth.UserAccount;

public class LoginUserUseCase {

    private final UserAccountRepository userAccountRepository;
    private final PasswordHasher passwordHasher;
    private final AuthTokenService authTokenService;

    public LoginUserUseCase(
            UserAccountRepository userAccountRepository,
            PasswordHasher passwordHasher,
            AuthTokenService authTokenService
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordHasher = passwordHasher;
        this.authTokenService = authTokenService;
    }

    public AuthSessionResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationAuthenticationException("Invalid email or password"));

        if (!passwordHasher.matches(request.password(), user.passwordHash())) {
            throw new ApplicationAuthenticationException("Invalid email or password");
        }

        String token = authTokenService.createToken(user.email(), List.of(user.role().name()));

        return new AuthSessionResponse(
                new AuthUserResponse(user.id(), user.email(), user.role(), user.profileCompleted()),
                token
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
