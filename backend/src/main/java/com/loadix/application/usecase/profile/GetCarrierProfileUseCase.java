package com.loadix.application.usecase.profile;

import java.util.Locale;

import com.loadix.application.dto.response.CarrierProfileResponse;
import com.loadix.application.port.in.GetCarrierProfilePort;
import com.loadix.application.port.out.CarrierProfilePort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.ProfileNotFoundException;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.model.UserAccount;

public class GetCarrierProfileUseCase implements GetCarrierProfilePort {

    private final UserAccountPort userAccountPort;
    private final CarrierProfilePort carrierProfilePort;

    public GetCarrierProfileUseCase(UserAccountPort userAccountPort, CarrierProfilePort carrierProfilePort) {
        this.userAccountPort = userAccountPort;
        this.carrierProfilePort = carrierProfilePort;
    }

    @Override
    public CarrierProfileResponse execute(String authenticatedEmail) {

        UserAccount user = userAccountPort.findByEmail(normalizeEmail(authenticatedEmail))
                .orElseThrow(UserNotFoundException::new);

        return carrierProfilePort.findByUserId(user.id())
                .map(profile -> new CarrierProfileResponse(
                        profile.name(),
                        profile.lastName(),
                        profile.phone(),
                        profile.vehicleType(),
                        profile.licensePlate(),
                        profile.carnet(),
                        profile.isCompleted()))
                .orElseThrow(ProfileNotFoundException::new);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
