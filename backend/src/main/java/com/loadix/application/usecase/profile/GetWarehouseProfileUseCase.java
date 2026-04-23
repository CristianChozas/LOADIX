package com.loadix.application.usecase.profile;

import java.util.Locale;

import com.loadix.application.dto.response.WarehouseProfileResponse;
import com.loadix.application.port.in.GetWarehouseProfilePort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.application.port.out.WarehouseProfilePort;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.model.UserAccount;

public class GetWarehouseProfileUseCase implements GetWarehouseProfilePort {
    
    private final UserAccountPort userAccountPort;
    private final WarehouseProfilePort warehouseProfilePort;

    public GetWarehouseProfileUseCase(UserAccountPort userAccountPort, WarehouseProfilePort warehouseProfilePort) {
        this.userAccountPort = userAccountPort;
        this.warehouseProfilePort = warehouseProfilePort;
    }

    @Override
    public WarehouseProfileResponse execute(String authenticatedEmail) {

        UserAccount user = userAccountPort.findByEmail(normalizeEmail(authenticatedEmail))
            .orElseThrow(UserNotFoundException::new);

        return warehouseProfilePort.findByUserId(user.id())
            .map(profile -> new WarehouseProfileResponse(
                profile.companyName(),
                profile.cif(),
                profile.address(),
                profile.postalCode(),
                profile.city(),
                profile.phone(),
                profile.contactPerson(),
                profile.cargoType(),
                profile.isCompleted()
            ))
            .orElseThrow(UserNotFoundException::new);

    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
