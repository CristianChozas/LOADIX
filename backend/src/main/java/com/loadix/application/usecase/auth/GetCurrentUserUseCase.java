package com.loadix.application.usecase.auth;

import java.util.Locale;
import java.util.Objects;

import com.loadix.application.dto.response.AuthUserResponse;
import com.loadix.application.mapper.AuthMapper;
import com.loadix.application.port.in.GetCurrentUserInputPort;
import com.loadix.application.port.out.UserAccountRepository;
import com.loadix.domain.exception.UserNotFoundException;

public class GetCurrentUserUseCase implements GetCurrentUserInputPort {

    private final UserAccountRepository userAccountRepository;
    private final AuthMapper authMapper;

    public GetCurrentUserUseCase(UserAccountRepository userAccountRepository, AuthMapper authMapper) {
        this.userAccountRepository = Objects.requireNonNull(userAccountRepository, "userAccountRepository cannot be null");
        this.authMapper = Objects.requireNonNull(authMapper, "authMapper cannot be null");
    }

    @Override
    public AuthUserResponse execute(String email) {
        var user = userAccountRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(UserNotFoundException::new);

        return authMapper.toAuthUserResponse(user);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
