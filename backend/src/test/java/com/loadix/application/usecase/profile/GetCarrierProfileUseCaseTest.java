package com.loadix.application.usecase.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.loadix.application.dto.response.CarrierProfileResponse;
import com.loadix.application.port.out.CarrierProfilePort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.ProfileNotFoundException;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.model.CarrierProfile;
import com.loadix.domain.model.UserAccount;
import com.loadix.domain.valueobject.UserRole;
import com.loadix.domain.valueobject.VehicleType;

@ExtendWith(MockitoExtension.class)
class GetCarrierProfileUseCaseTest {

    @Mock
    private UserAccountPort userAccountPort;

    @Mock
    private CarrierProfilePort carrierProfilePort;

    private GetCarrierProfileUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetCarrierProfileUseCase(userAccountPort, carrierProfilePort);
    }

    @Test
    void returnsCarrierProfileForAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UserAccount account = new UserAccount(userId, "carrier@loadix.test", "hashed", UserRole.CARRIER, false);
        CarrierProfile profile = new CarrierProfile(userId, "Carlos", "Gomez", "612345678", VehicleType.FURGONETA,
                "1234ABC", "C1");

        when(userAccountPort.findByEmail("carrier@loadix.test")).thenReturn(Optional.of(account));
        when(carrierProfilePort.findByUserId(userId)).thenReturn(Optional.of(profile));

        CarrierProfileResponse response = useCase.execute(" CARRIER@LOADIX.TEST ");

        assertThat(response.name()).isEqualTo("Carlos");
        assertThat(response.lastName()).isEqualTo("Gomez");
        assertThat(response.vehicleType()).isEqualTo(VehicleType.FURGONETA);
        assertThat(response.completed()).isTrue();
    }

    @Test
    void throwsWhenAuthenticatedUserDoesNotExist() {
        when(userAccountPort.findByEmail("carrier@loadix.test")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("carrier@loadix.test"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void throwsWhenCarrierProfileDoesNotExist() {
        UUID userId = UUID.randomUUID();
        UserAccount account = new UserAccount(userId, "carrier@loadix.test", "hashed", UserRole.CARRIER, false);
        when(userAccountPort.findByEmail("carrier@loadix.test")).thenReturn(Optional.of(account));
        when(carrierProfilePort.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("carrier@loadix.test"))
                .isInstanceOf(ProfileNotFoundException.class);
    }
}
