package com.loadix.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.loadix.application.port.out.AuthTokenService;
import com.loadix.application.port.out.PasswordHasher;
import com.loadix.application.port.out.UserAccountRepository;
import com.loadix.application.usecase.GetCurrentUserUseCase;
import com.loadix.application.usecase.LoginUserUseCase;
import com.loadix.application.usecase.RegisterUserUseCase;

@Configuration
@EnableConfigurationProperties({SecurityProperties.class, RateLimitProperties.class})
public class LoadixConfiguration {

    @Bean
    RegisterUserUseCase registerUserUseCase(UserAccountRepository userAccountRepository, PasswordHasher passwordHasher) {
        return new RegisterUserUseCase(userAccountRepository, passwordHasher);
    }

    @Bean
    LoginUserUseCase loginUserUseCase(
            UserAccountRepository userAccountRepository,
            PasswordHasher passwordHasher,
            AuthTokenService authTokenService
    ) {
        return new LoginUserUseCase(userAccountRepository, passwordHasher, authTokenService);
    }

    @Bean
    GetCurrentUserUseCase getCurrentUserUseCase(UserAccountRepository userAccountRepository) {
        return new GetCurrentUserUseCase(userAccountRepository);
    }
}
