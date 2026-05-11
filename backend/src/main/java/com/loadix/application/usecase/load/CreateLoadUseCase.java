package com.loadix.application.usecase.load;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.loadix.application.dto.request.CreateLoadRequest;
import com.loadix.application.dto.response.LoadResponse;
import com.loadix.application.port.in.CreateLoadPort;
import com.loadix.application.port.out.LoadPort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.application.port.out.WarehouseProfilePort;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.model.LoadPublication;
import com.loadix.domain.model.UserAccount;
import com.loadix.domain.valueobject.LoadStatus;
import com.loadix.domain.valueobject.UserRole;

public class CreateLoadUseCase implements CreateLoadPort {

    private final UserAccountPort userAccountPort;
    private final WarehouseProfilePort warehouseProfilePort;
    private final LoadPort loadPort;

    public CreateLoadUseCase(
        UserAccountPort userAccountPort,
        WarehouseProfilePort warehouseProfilePort,
        LoadPort loadPort
    ) {
        this.userAccountPort = userAccountPort;
        this.warehouseProfilePort = warehouseProfilePort;
        this.loadPort = loadPort;
    }

    @Override
    public LoadResponse execute(String authenticatedEmail, CreateLoadRequest request) {
        UserAccount user = userAccountPort.findByEmail(normalizeEmail(authenticatedEmail))
            .orElseThrow(UserNotFoundException::new);

        if (user.role() != UserRole.WAREHOUSE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only warehouse users can publish loads");
        }

        var warehouseProfile = warehouseProfilePort.findByUserId(user.id())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Warehouse profile is required before publishing loads"));

        if (!warehouseProfile.isCompleted()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Warehouse profile must be completed before publishing loads");
        }

        LoadPublication loadToSave = new LoadPublication(
            user.id(),
            user.id(),
            request.origin().address().trim(),
            request.origin().city().trim(),
            request.origin().postalCode().trim(),
            request.destination().address().trim(),
            request.destination().city().trim(),
            request.destination().postalCode().trim(),
            request.cargoType(),
            request.weightKg(),
            request.loadQuantity(),
            request.loadUnitType(),
            request.pickupDate(),
            request.basePriceAmount(),
            normalizeOptional(request.notes()),
            normalizeOptional(request.specialRequirements()),
            LoadStatus.PUBLISHED
        );

        var saved = loadPort.save(loadToSave);

        return new LoadResponse(
            saved.id(),
            saved.warehouseUserId(),
            saved.createdByUserId(),
            new LoadResponse.LocationResponse(saved.originAddress(), saved.originCity(), saved.originPostalCode()),
            new LoadResponse.LocationResponse(saved.destinationAddress(), saved.destinationCity(), saved.destinationPostalCode()),
            saved.cargoType(),
            saved.weightKg(),
            saved.loadQuantity(),
            saved.loadUnitType(),
            saved.pickupDate(),
            saved.basePriceAmount(),
            saved.notes(),
            saved.specialRequirements(),
            saved.status(),
            saved.createdAt(),
            saved.updatedAt()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
