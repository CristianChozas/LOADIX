package com.loadix.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.loadix.application.port.out.PasswordHasher;
import com.loadix.application.port.out.UserAccountRepository;
import com.loadix.application.usecase.RegisterUserUseCase;

@Configuration
@EnableConfigurationProperties({SecurityProperties.class, RateLimitProperties.class})
public class LoadixConfiguration {

    @Bean
    RegisterUserUseCase registerUserUseCase(UserAccountRepository userAccountRepository, PasswordHasher passwordHasher) {
        return new RegisterUserUseCase(userAccountRepository, passwordHasher);
    }
}
