package com.loadix.application.usecase.auth;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.loadix.application.dto.request.UpdateEmailRequest;
import com.loadix.application.dto.response.AuthSessionResponse;
import com.loadix.application.mapper.AuthMapper;
import com.loadix.application.port.in.UpdateCurrentUserEmailPort;
import com.loadix.application.port.out.AuthTokenPort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.UserAlreadyExistsException;
import com.loadix.domain.model.UserAccount;

public class UpdateCurrentUserEmailUseCase implements UpdateCurrentUserEmailPort {

    private final UserAccountPort userAccountPort;
    private final AuthTokenPort authTokenPort;
    private final AuthMapper authMapper;

    public UpdateCurrentUserEmailUseCase(
            UserAccountPort userAccountPort,
            AuthTokenPort authTokenPort,
            AuthMapper authMapper
    ) {
        this.userAccountPort = Objects.requireNonNull(userAccountPort, "userAccountPort cannot be null");
        this.authTokenPort = Objects.requireNonNull(authTokenPort, "authTokenPort cannot be null");
        this.authMapper = Objects.requireNonNull(authMapper, "authMapper cannot be null");
    }

    @Override
    public AuthSessionResponse execute(String currentEmail, UpdateEmailRequest request) {
        String normalizedCurrentEmail = normalizeEmail(currentEmail);
        String normalizedNewEmail = normalizeEmail(Objects.requireNonNull(request, "request cannot be null").email());

        if (!normalizedCurrentEmail.equals(normalizedNewEmail) && userAccountPort.existsByEmail(normalizedNewEmail)) {
            throw new UserAlreadyExistsException();
        }

        UserAccount updatedUser = userAccountPort.updateEmail(normalizedCurrentEmail, normalizedNewEmail);
        String token = authTokenPort.createToken(updatedUser.email(), List.of(updatedUser.role().name()));

        return new AuthSessionResponse(authMapper.toAuthUserResponse(updatedUser), token);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
