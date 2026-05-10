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

import com.loadix.application.dto.request.UpdateCarrierProfileRequest;
import com.loadix.application.dto.response.CarrierProfileResponse;
import com.loadix.application.port.out.CarrierProfilePort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.model.CarrierProfile;
import com.loadix.domain.model.UserAccount;
import com.loadix.domain.valueobject.UserRole;
import com.loadix.domain.valueobject.VehicleType;

@ExtendWith(MockitoExtension.class)
class UpdateCarrierProfileUseCaseTest {

    @Mock
    private UserAccountPort userAccountPort;

    @Mock
    private CarrierProfilePort carrierProfilePort;

    private UpdateCarrierProfileUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateCarrierProfileUseCase(userAccountPort, carrierProfilePort);
    }

    @Test
    void updatesCarrierProfileForAuthenticatedUser() {
        UserAccount account = new UserAccount(UUID.randomUUID(), "carrier@loadix.test", "hashed", UserRole.CARRIER, true);
        when(userAccountPort.findByEmail("carrier@loadix.test")).thenReturn(Optional.of(account));
        when(carrierProfilePort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CarrierProfileResponse response = useCase.execute(
                " CARRIER@LOADIX.TEST ",
                new UpdateCarrierProfileRequest("Laura", "Martin", "699999999", VehicleType.TRAILER, "9999ZZZ", "C+E")
        );

        ArgumentCaptor<CarrierProfile> captor = ArgumentCaptor.forClass(CarrierProfile.class);
        verify(carrierProfilePort).save(captor.capture());

        assertThat(captor.getValue().id()).isEqualTo(account.id());
        assertThat(captor.getValue().name()).isEqualTo("Laura");
        assertThat(captor.getValue().vehicleType()).isEqualTo(VehicleType.TRAILER);
        assertThat(response.name()).isEqualTo("Laura");
        assertThat(response.vehicleType()).isEqualTo(VehicleType.TRAILER);
        assertThat(response.completed()).isTrue();
    }

    @Test
    void throwsWhenAuthenticatedUserDoesNotExist() {
        when(userAccountPort.findByEmail("carrier@loadix.test")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                "carrier@loadix.test",
                new UpdateCarrierProfileRequest("Laura", "Martin", "699999999", VehicleType.TRAILER, "9999ZZZ", "C+E")
        )).isInstanceOf(UserNotFoundException.class);
    }
}
