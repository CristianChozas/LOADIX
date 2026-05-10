package com.loadix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.loadix.application.mapper.AuthMapper;
import com.loadix.application.port.in.GetCurrentUserPort;
import com.loadix.application.port.in.LoginUserPort;
import com.loadix.application.port.in.RegisterUserPort;
import com.loadix.application.port.in.UpdateCurrentUserEmailPort;
import com.loadix.application.port.out.AuthTokenPort;
import com.loadix.application.port.out.PasswordHasherPort;
import com.loadix.application.port.out.UserAccountPort;
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
    public RegisterUserPort registerUserPort(
            UserAccountPort userAccountPort,
            PasswordHasherPort passwordHasherPort,
            AuthMapper authMapper
    ) {
        return new RegisterUserUseCase(userAccountPort, passwordHasherPort, authMapper);
    }

    @Bean
    public LoginUserPort loginUserPort(
            UserAccountPort userAccountPort,
            PasswordHasherPort passwordHasherPort,
            AuthTokenPort authTokenPort,
            AuthMapper authMapper
    ) {
        return new LoginUserUseCase(userAccountPort, passwordHasherPort, authTokenPort, authMapper);
    }

    @Bean
    public GetCurrentUserPort getCurrentUserPort(
            UserAccountPort userAccountPort,
            AuthMapper authMapper
    ) {
        return new GetCurrentUserUseCase(userAccountPort, authMapper);
    }

    @Bean
    public UpdateCurrentUserEmailPort updateCurrentUserEmailPort(
            UserAccountPort userAccountPort,
            AuthTokenPort authTokenPort,
            AuthMapper authMapper
    ) {
        return new UpdateCurrentUserEmailUseCase(userAccountPort, authTokenPort, authMapper);
    }

}
