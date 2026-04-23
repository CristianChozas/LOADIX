package com.loadix.application.usecase.profile;

import java.util.Locale;

import com.loadix.application.dto.request.CreateWarehouseProfileRequest;
import com.loadix.application.dto.response.WarehouseProfileResponse;
import com.loadix.application.port.in.CreateWarehouseProfilePort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.application.port.out.WarehouseProfilePort;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.model.UserAccount;
import com.loadix.domain.model.WarehouseProfile;

public class CreateWarehouseProfileUseCase implements CreateWarehouseProfilePort {
    private final UserAccountPort userAccountPort;
    private final WarehouseProfilePort warehouseProfilePort;

    public CreateWarehouseProfileUseCase(UserAccountPort userAccountPort, WarehouseProfilePort warehouseProfilePort) {
        this.userAccountPort = userAccountPort;
        this.warehouseProfilePort = warehouseProfilePort;
    }

    @Override
    public WarehouseProfileResponse execute(String authenticatedEmail, CreateWarehouseProfileRequest request) {
        
        UserAccount user = userAccountPort.findByEmail(normalizeEmail(authenticatedEmail))
            .orElseThrow(UserNotFoundException::new);
            
        WarehouseProfile profileToSave = new WarehouseProfile(
            user.id(),
            request.companyName(),
            request.cif(),
            request.address(),
            request.postalCode(),
            request.city(),
            request.phone(),
            request.contactPerson(),
            request.cargoType()
        );

        WarehouseProfile savedProfile = warehouseProfilePort.save(profileToSave);

        return new WarehouseProfileResponse(
            savedProfile.companyName(),
            savedProfile.cif(),
            savedProfile.address(),
            savedProfile.postalCode(),
            savedProfile.city(),
            savedProfile.phone(),
            savedProfile.contactPerson(),
            savedProfile.cargoType(),
            savedProfile.isCompleted()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

}
