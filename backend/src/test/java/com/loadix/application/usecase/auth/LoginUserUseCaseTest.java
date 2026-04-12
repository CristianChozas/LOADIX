package com.loadix.application.usecase.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.loadix.application.dto.request.LoginRequest;
import com.loadix.application.mapper.AuthMapper;
import com.loadix.domain.exception.InvalidCredentialsException;
import com.loadix.application.port.out.AuthTokenService;
import com.loadix.application.port.out.PasswordHasher;
import com.loadix.application.port.out.UserAccountRepository;
import com.loadix.domain.model.UserAccount;
import com.loadix.domain.valueobject.UserRole;

@ExtendWith(MockitoExtension.class)
class LoginUserUseCaseTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private AuthTokenService authTokenService;

    private LoginUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new LoginUserUseCase(userAccountRepository, passwordHasher, authTokenService, new AuthMapper());
    }

    @Test
    void logsInAndReturnsSession() {
        UserAccount account = new UserAccount(UUID.randomUUID(), "login@loadix.test", "hashed-password", UserRole.WAREHOUSE, false);
        when(userAccountRepository.findByEmail("login@loadix.test")).thenReturn(Optional.of(account));
        when(passwordHasher.matches("Password1", "hashed-password")).thenReturn(true);
        when(authTokenService.createToken("login@loadix.test", List.of("WAREHOUSE"))).thenReturn("jwt-token");

        var session = useCase.execute(new LoginRequest(" LOGIN@LOADIX.TEST ", "Password1"));

        assertThat(session.user().email()).isEqualTo("login@loadix.test");
        assertThat(session.token()).isEqualTo("jwt-token");
    }

    @Test
    void rejectsInvalidCredentials() {
        when(userAccountRepository.findByEmail("missing@loadix.test")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new LoginRequest("missing@loadix.test", "Password1")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
