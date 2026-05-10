package com.loadix.application.usecase.auth;

import java.util.Locale;
import java.util.Objects;

import com.loadix.application.dto.response.AuthUserResponse;
import com.loadix.application.mapper.AuthMapper;
import com.loadix.application.port.in.GetCurrentUserPort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.UserNotFoundException;

public class GetCurrentUserUseCase implements GetCurrentUserPort {

    private final UserAccountPort userAccountPort;
    private final AuthMapper authMapper;

    public GetCurrentUserUseCase(UserAccountPort userAccountPort, AuthMapper authMapper) {
        this.userAccountPort = Objects.requireNonNull(userAccountPort, "userAccountPort cannot be null");
        this.authMapper = Objects.requireNonNull(authMapper, "authMapper cannot be null");
    }

    @Override
    public AuthUserResponse execute(String email) {
        var user = userAccountPort.findByEmail(normalizeEmail(email))
                .orElseThrow(UserNotFoundException::new);

        return authMapper.toAuthUserResponse(user);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
