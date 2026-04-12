package com.loadix.application.usecase;

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
import com.loadix.application.exception.ApplicationConflictException;
import com.loadix.application.port.out.PasswordHasher;
import com.loadix.application.port.out.UserAccountRepository;
import com.loadix.domain.auth.UserAccount;
import com.loadix.domain.auth.UserRole;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PasswordHasher passwordHasher;

    private RegisterUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCase(userAccountRepository, passwordHasher);
    }

    @Test
    void registersNewUserWithHashedPassword() {
        when(userAccountRepository.existsByEmail("register@loadix.test")).thenReturn(false);
        when(passwordHasher.hash("Password1")).thenReturn("hashed-password");
        when(userAccountRepository.save(any())).thenAnswer(invocation -> {
            UserAccount account = invocation.getArgument(0);
            return new UserAccount(UUID.randomUUID(), account.email(), account.passwordHash(), account.role(), false);
        });

        var response = useCase.register(new RegisterRequest(" Register@Loadix.Test ", "Password1", UserRole.WAREHOUSE));

        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userAccountRepository).save(captor.capture());
        assertThat(captor.getValue().email()).isEqualTo("register@loadix.test");
        assertThat(captor.getValue().passwordHash()).isEqualTo("hashed-password");
        assertThat(response.email()).isEqualTo("register@loadix.test");
        assertThat(response.role()).isEqualTo(UserRole.WAREHOUSE);
    }

    @Test
    void rejectsDuplicateEmail() {
        when(userAccountRepository.existsByEmail("duplicate@loadix.test")).thenReturn(true);

        assertThatThrownBy(
                () -> useCase.register(new RegisterRequest("duplicate@loadix.test", "Password1", UserRole.CARRIER)))
                .isInstanceOf(ApplicationConflictException.class);
    }
}
