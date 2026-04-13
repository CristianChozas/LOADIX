package com.loadix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.loadix.application.mapper.AuthMapper;
import com.loadix.application.port.in.GetCurrentUserInputPort;
import com.loadix.application.port.in.LoginUserInputPort;
import com.loadix.application.port.in.RegisterUserInputPort;
import com.loadix.application.port.in.UpdateCurrentUserEmailInputPort;
import com.loadix.application.port.out.AuthTokenService;
import com.loadix.application.port.out.PasswordHasher;
import com.loadix.application.port.out.UserAccountRepository;
import com.loadix.application.usecase.auth.GetCurrentUserUseCase;
import com.loadix.application.usecase.auth.LoginUserUseCase;
import com.loadix.application.usecase.auth.RegisterUserUseCase;
import com.loadix.application.usecase.auth.UpdateCurrentUserEmailUseCase;

@Configuration
public class AuthConfig {

    @Bean
    public AuthMapper authMapper() {
        return new AuthMapper();
    }

    @Bean
    public RegisterUserInputPort registerUserInputPort(
            UserAccountRepository userAccountRepository,
            PasswordHasher passwordHasher,
            AuthMapper authMapper
    ) {
        return new RegisterUserUseCase(userAccountRepository, passwordHasher, authMapper);
    }

    @Bean
    public LoginUserInputPort loginUserInputPort(
            UserAccountRepository userAccountRepository,
            PasswordHasher passwordHasher,
            AuthTokenService authTokenService,
            AuthMapper authMapper
    ) {
        return new LoginUserUseCase(userAccountRepository, passwordHasher, authTokenService, authMapper);
    }

    @Bean
    public GetCurrentUserInputPort getCurrentUserInputPort(
            UserAccountRepository userAccountRepository,
            AuthMapper authMapper
    ) {
        return new GetCurrentUserUseCase(userAccountRepository, authMapper);
    }

    @Bean
    public UpdateCurrentUserEmailInputPort updateCurrentUserEmailInputPort(
            UserAccountRepository userAccountRepository,
            AuthTokenService authTokenService,
            AuthMapper authMapper
    ) {
        return new UpdateCurrentUserEmailUseCase(userAccountRepository, authTokenService, authMapper);
    }
}
