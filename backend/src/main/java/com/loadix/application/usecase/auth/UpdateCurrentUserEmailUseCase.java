package com.loadix.application.usecase.auth;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.loadix.application.dto.request.UpdateEmailRequest;
import com.loadix.application.dto.response.AuthSessionResponse;
import com.loadix.application.mapper.AuthMapper;
import com.loadix.application.port.in.UpdateCurrentUserEmailInputPort;
import com.loadix.application.port.out.AuthTokenService;
import com.loadix.application.port.out.UserAccountRepository;
import com.loadix.domain.exception.UserAlreadyExistsException;
import com.loadix.domain.model.UserAccount;

public class UpdateCurrentUserEmailUseCase implements UpdateCurrentUserEmailInputPort {

    private final UserAccountRepository userAccountRepository;
    private final AuthTokenService authTokenService;
    private final AuthMapper authMapper;

    public UpdateCurrentUserEmailUseCase(
            UserAccountRepository userAccountRepository,
            AuthTokenService authTokenService,
            AuthMapper authMapper
    ) {
        this.userAccountRepository = Objects.requireNonNull(userAccountRepository, "userAccountRepository cannot be null");
        this.authTokenService = Objects.requireNonNull(authTokenService, "authTokenService cannot be null");
        this.authMapper = Objects.requireNonNull(authMapper, "authMapper cannot be null");
    }

    @Override
    public AuthSessionResponse execute(String currentEmail, UpdateEmailRequest request) {
        String normalizedCurrentEmail = normalizeEmail(currentEmail);
        String normalizedNewEmail = normalizeEmail(Objects.requireNonNull(request, "request cannot be null").email());

        if (!normalizedCurrentEmail.equals(normalizedNewEmail) && userAccountRepository.existsByEmail(normalizedNewEmail)) {
            throw new UserAlreadyExistsException();
        }

        UserAccount updatedUser = userAccountRepository.updateEmail(normalizedCurrentEmail, normalizedNewEmail);
        String token = authTokenService.createToken(updatedUser.email(), List.of(updatedUser.role().name()));

        return new AuthSessionResponse(authMapper.toAuthUserResponse(updatedUser), token);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
