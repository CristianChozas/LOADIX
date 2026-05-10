package com.loadix.application.usecase.profile;

import java.util.Locale;

import com.loadix.application.dto.request.CreateCarrierProfileRequest;
import com.loadix.application.dto.response.CarrierProfileResponse;
import com.loadix.application.port.in.CreateCarrierProfilePort;
import com.loadix.application.port.out.CarrierProfilePort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.model.CarrierProfile;
import com.loadix.domain.model.UserAccount;

public class CreateCarrierProfileUseCase implements CreateCarrierProfilePort {

    private final UserAccountPort userAccountPort;
    private final CarrierProfilePort carrierProfilePort;

    public CreateCarrierProfileUseCase(UserAccountPort userAccountPort, CarrierProfilePort carrierProfilePort) {
        this.userAccountPort = userAccountPort;
        this.carrierProfilePort = carrierProfilePort;
    }

    @Override
    public CarrierProfileResponse execute(String authenticatedEmail, CreateCarrierProfileRequest request) {

        UserAccount user = userAccountPort.findByEmail(normalizeEmail(authenticatedEmail))
                .orElseThrow(UserNotFoundException::new);

        CarrierProfile profileToSave = new CarrierProfile(
                user.id(),
                request.name(),
                request.lastName(),
                request.phone(),
                request.vehicleType(),
                request.licensePlate(),
                request.carnet());

        CarrierProfile savedProfile = carrierProfilePort.save(profileToSave);

        return new CarrierProfileResponse (
            savedProfile.name(),
            savedProfile.lastName(),
            savedProfile.phone(),
            savedProfile.vehicleType(),
            savedProfile.licensePlate(),
            savedProfile.carnet(),
            savedProfile.isCompleted());
    }

    
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

}
