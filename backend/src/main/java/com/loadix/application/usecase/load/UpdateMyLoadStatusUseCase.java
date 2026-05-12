package com.loadix.application.usecase.load;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.loadix.application.dto.request.UpdateLoadStatusRequest;
import com.loadix.application.dto.response.LoadResponse;
import com.loadix.application.port.in.UpdateMyLoadStatusPort;
import com.loadix.application.port.out.LoadPort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.model.LoadPublication;
import com.loadix.domain.model.PersistedLoadPublication;
import com.loadix.domain.model.UserAccount;
import com.loadix.domain.valueobject.LoadStatus;
import com.loadix.domain.valueobject.UserRole;

public class UpdateMyLoadStatusUseCase implements UpdateMyLoadStatusPort {

    private static final Map<LoadStatus, Set<LoadStatus>> ALLOWED_TRANSITIONS = Map.of(
        LoadStatus.PUBLISHED, EnumSet.of(LoadStatus.RESERVED, LoadStatus.CANCELLED),
        LoadStatus.RESERVED, EnumSet.of(LoadStatus.IN_TRANSIT, LoadStatus.CANCELLED),
        LoadStatus.IN_TRANSIT, EnumSet.of(LoadStatus.DELIVERED),
        LoadStatus.DELIVERED, EnumSet.noneOf(LoadStatus.class),
        LoadStatus.CANCELLED, EnumSet.noneOf(LoadStatus.class)
    );

    private final UserAccountPort userAccountPort;
    private final LoadPort loadPort;

    public UpdateMyLoadStatusUseCase(UserAccountPort userAccountPort, LoadPort loadPort) {
        this.userAccountPort = userAccountPort;
        this.loadPort = loadPort;
    }

    @Override
    public LoadResponse execute(String authenticatedEmail, UUID loadId, UpdateLoadStatusRequest request) {
        UserAccount user = userAccountPort.findByEmail(normalizeEmail(authenticatedEmail))
            .orElseThrow(UserNotFoundException::new);

        if (user.role() != UserRole.WAREHOUSE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only warehouse users can update load status");
        }

        PersistedLoadPublication existing = loadPort.findById(loadId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Load not found"));

        if (!existing.warehouseUserId().equals(user.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot update status for a load from another warehouse");
        }

        LoadStatus targetStatus = request.status();
        LoadStatus currentStatus = existing.status();
        Set<LoadStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of());

        if (!allowed.contains(targetStatus)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid status transition from " + currentStatus + " to " + targetStatus
            );
        }

        LoadPublication updatedLoad = new LoadPublication(
            existing.warehouseUserId(),
            existing.createdByUserId(),
            existing.originAddress(),
            existing.originCity(),
            existing.originPostalCode(),
            existing.destinationAddress(),
            existing.destinationCity(),
            existing.destinationPostalCode(),
            existing.cargoType(),
            existing.weightKg(),
            existing.loadQuantity(),
            existing.loadUnitType(),
            existing.pickupDate(),
            existing.basePriceAmount(),
            existing.notes(),
            existing.specialRequirements(),
            targetStatus
        );

        PersistedLoadPublication saved = loadPort.updateById(loadId, updatedLoad);
        return toResponse(saved);
    }

    private LoadResponse toResponse(PersistedLoadPublication saved) {
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
}
