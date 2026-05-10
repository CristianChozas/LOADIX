package com.loadix.application.usecase.profile;

import java.util.Locale;

import com.loadix.application.dto.request.UpdateCarrierProfileRequest;
import com.loadix.application.dto.response.CarrierProfileResponse;
import com.loadix.application.port.in.UpdateCarrierProfilePort;
import com.loadix.application.port.out.CarrierProfilePort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.model.CarrierProfile;
import com.loadix.domain.model.UserAccount;

public class UpdateCarrierProfileUseCase implements UpdateCarrierProfilePort {

    private final UserAccountPort userAccountPort;
    private final CarrierProfilePort carrierProfilePort;

    public UpdateCarrierProfileUseCase(UserAccountPort userAccountPort, CarrierProfilePort carrierProfilePort) {
        this.userAccountPort = userAccountPort;
        this.carrierProfilePort = carrierProfilePort;
    }

    @Override
    public CarrierProfileResponse execute(String authenticatedEmail, UpdateCarrierProfileRequest request) {
        UserAccount user = userAccountPort.findByEmail(normalizeEmail(authenticatedEmail))
                .orElseThrow(UserNotFoundException::new);

        CarrierProfile profileToUpdate = new CarrierProfile(
                user.id(),
                request.name(),
                request.lastName(),
                request.phone(),
                request.vehicleType(),
                request.licensePlate(),
                request.carnet());

        CarrierProfile updatedProfile = carrierProfilePort.save(profileToUpdate);

        return new CarrierProfileResponse(
                updatedProfile.name(),
                updatedProfile.lastName(),
                updatedProfile.phone(),
                updatedProfile.vehicleType(),
                updatedProfile.licensePlate(),
                updatedProfile.carnet(),
                updatedProfile.isCompleted());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
