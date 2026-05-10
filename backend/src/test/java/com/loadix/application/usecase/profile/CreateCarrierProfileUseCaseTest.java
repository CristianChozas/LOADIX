package com.loadix.application.usecase.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.loadix.application.dto.request.CreateCarrierProfileRequest;
import com.loadix.application.dto.response.CarrierProfileResponse;
import com.loadix.application.port.out.CarrierProfilePort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.model.CarrierProfile;
import com.loadix.domain.model.UserAccount;
import com.loadix.domain.valueobject.VehicleType;
import com.loadix.domain.valueobject.UserRole;

@ExtendWith(MockitoExtension.class)
class CreateCarrierProfileUseCaseTest {

    @Mock
    private UserAccountPort userAccountPort;

    @Mock
    private CarrierProfilePort carrierProfilePort;

    private CreateCarrierProfileUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateCarrierProfileUseCase(userAccountPort, carrierProfilePort);
    }

    @Test
    void createsCarrierProfileForAuthenticatedUser() {
        UserAccount account = new UserAccount(UUID.randomUUID(), "carrier@loadix.test", "hashed", UserRole.CARRIER, false);
        when(userAccountPort.findByEmail("carrier@loadix.test")).thenReturn(Optional.of(account));
        when(carrierProfilePort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CarrierProfileResponse response = useCase.execute(
                " CARRIER@LOADIX.TEST ",
                new CreateCarrierProfileRequest("Carlos", "Gomez", "612345678", VehicleType.FURGONETA, "1234ABC", "C1")
        );

        ArgumentCaptor<CarrierProfile> captor = ArgumentCaptor.forClass(CarrierProfile.class);
        verify(carrierProfilePort).save(captor.capture());

        assertThat(captor.getValue().id()).isEqualTo(account.id());
        assertThat(captor.getValue().name()).isEqualTo("Carlos");
        assertThat(captor.getValue().vehicleType()).isEqualTo(VehicleType.FURGONETA);
        assertThat(response.name()).isEqualTo("Carlos");
        assertThat(response.vehicleType()).isEqualTo(VehicleType.FURGONETA);
        assertThat(response.completed()).isTrue();
    }

    @Test
    void throwsWhenAuthenticatedUserDoesNotExist() {
        when(userAccountPort.findByEmail("carrier@loadix.test")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                "carrier@loadix.test",
                new CreateCarrierProfileRequest("Carlos", "Gomez", "612345678", VehicleType.FURGONETA, "1234ABC", "C1")
        )).isInstanceOf(UserNotFoundException.class);
    }
}
