package com.loadix.application.usecase.profile;

import java.util.Locale;

import com.loadix.application.dto.request.UpdateWarehouseProfileRequest;
import com.loadix.application.dto.response.WarehouseProfileResponse;
import com.loadix.application.port.in.UpdateWarehouseProfilePort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.application.port.out.WarehouseProfilePort;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.model.UserAccount;
import com.loadix.domain.model.WarehouseProfile;

public class UpdateWarehouseProfileUseCase implements UpdateWarehouseProfilePort {
    
    private final UserAccountPort userAccountPort;
    private final WarehouseProfilePort warehouseProfilePort;

    public UpdateWarehouseProfileUseCase(UserAccountPort userAccountPort, WarehouseProfilePort warehouseProfilePort) {
        this.userAccountPort = userAccountPort;
        this.warehouseProfilePort = warehouseProfilePort;
    }

    @Override
    public WarehouseProfileResponse execute(String emailAuthenticated, UpdateWarehouseProfileRequest updateWarehouseProfileRequest) {
        UserAccount user = userAccountPort.findByEmail(normalizeEmail(emailAuthenticated))
            .orElseThrow(UserNotFoundException::new);

        WarehouseProfile profileToUpdate = new WarehouseProfile(
            user.id(),
            updateWarehouseProfileRequest.companyName(),
            updateWarehouseProfileRequest.cif(),
            updateWarehouseProfileRequest.address(),
            updateWarehouseProfileRequest.postalCode(),
            updateWarehouseProfileRequest.city(),
            updateWarehouseProfileRequest.phone(),
            updateWarehouseProfileRequest.contactPerson(),
            updateWarehouseProfileRequest.cargoType()
        );

        WarehouseProfile updatedProfile = warehouseProfilePort.save(profileToUpdate);

        return new WarehouseProfileResponse(
            updatedProfile.companyName(),
            updatedProfile.cif(),
            updatedProfile.address(),
            updatedProfile.postalCode(),
            updatedProfile.city(),
            updatedProfile.phone(),
            updatedProfile.contactPerson(),
            updatedProfile.cargoType(),
            updatedProfile.isCompleted()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

}
