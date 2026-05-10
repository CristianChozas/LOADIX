package com.loadix.application.usecase.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.loadix.application.dto.request.RegisterRequest;
import com.loadix.application.port.out.PasswordHasherPort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.application.mapper.AuthMapper;
import com.loadix.domain.exception.UserAlreadyExistsException;
import com.loadix.domain.model.UserAccount;
import com.loadix.domain.valueobject.UserRole;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserAccountPort userAccountPort;

    @Mock
    private PasswordHasherPort passwordHasherPort;

    private RegisterUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCase(userAccountPort, passwordHasherPort, new AuthMapper());
    }

    @Test
    void registersNewUserWithHashedPassword() {
        when(userAccountPort.existsByEmail("register@loadix.test")).thenReturn(false);
        when(passwordHasherPort.hash("Password1")).thenReturn("hashed-password");
        when(userAccountPort.save(any())).thenAnswer(invocation -> {
            UserAccount account = invocation.getArgument(0);
            return new UserAccount(UUID.randomUUID(), account.email(), account.passwordHash(), account.role(), false);
        });

        var response = useCase.execute(new RegisterRequest(" Register@Loadix.Test ", "Password1", UserRole.WAREHOUSE));

        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userAccountPort).save(captor.capture());
        assertThat(captor.getValue().email()).isEqualTo("register@loadix.test");
        assertThat(captor.getValue().passwordHash()).isEqualTo("hashed-password");
        assertThat(response.email()).isEqualTo("register@loadix.test");
        assertThat(response.role()).isEqualTo(UserRole.WAREHOUSE);
    }

    @Test
    void rejectsDuplicateEmail() {
        when(userAccountPort.existsByEmail("duplicate@loadix.test")).thenReturn(true);

        assertThatThrownBy(
                () -> useCase.execute(new RegisterRequest("duplicate@loadix.test", "Password1", UserRole.CARRIER)))
                .isInstanceOf(UserAlreadyExistsException.class);
    }
}
