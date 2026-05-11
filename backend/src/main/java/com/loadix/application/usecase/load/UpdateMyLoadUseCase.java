package com.loadix.application.usecase.load;

import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.loadix.application.dto.request.UpdateLoadRequest;
import com.loadix.application.dto.response.LoadResponse;
import com.loadix.application.port.in.UpdateMyLoadPort;
import com.loadix.application.port.out.LoadPort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.model.LoadPublication;
import com.loadix.domain.model.UserAccount;
import com.loadix.domain.valueobject.LoadStatus;
import com.loadix.domain.valueobject.UserRole;

public class UpdateMyLoadUseCase implements UpdateMyLoadPort {

    private final UserAccountPort userAccountPort;
    private final LoadPort loadPort;

    public UpdateMyLoadUseCase(UserAccountPort userAccountPort, LoadPort loadPort) {
        this.userAccountPort = userAccountPort;
        this.loadPort = loadPort;
    }

    @Override
    public LoadResponse execute(String authenticatedEmail, UUID loadId, UpdateLoadRequest request) {
        UserAccount user = userAccountPort.findByEmail(normalizeEmail(authenticatedEmail))
            .orElseThrow(UserNotFoundException::new);

        if (user.role() != UserRole.WAREHOUSE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only warehouse users can update published loads");
        }

        var existing = loadPort.findById(loadId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Load not found"));

        if (!existing.warehouseUserId().equals(user.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot edit a load from another warehouse");
        }

        if (existing.status() != LoadStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only published loads can be edited");
        }

        LoadPublication updatedLoad = new LoadPublication(
            existing.warehouseUserId(),
            existing.createdByUserId(),
            request.origin().address().trim(),
            request.origin().city().trim(),
            request.origin().postalCode().trim(),
            request.destination().address().trim(),
            request.destination().city().trim(),
            request.destination().postalCode().trim(),
            request.cargoType(),
            request.weightKg(),
            request.pickupDate(),
            request.basePriceAmount(),
            normalizeOptional(request.notes()),
            normalizeOptional(request.specialRequirements()),
            existing.status()
        );

        var saved = loadPort.updateById(loadId, updatedLoad);

        return new LoadResponse(
            saved.id(),
            saved.warehouseUserId(),
            saved.createdByUserId(),
            new LoadResponse.LocationResponse(saved.originAddress(), saved.originCity(), saved.originPostalCode()),
            new LoadResponse.LocationResponse(saved.destinationAddress(), saved.destinationCity(), saved.destinationPostalCode()),
            saved.cargoType(),
            saved.weightKg(),
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
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
