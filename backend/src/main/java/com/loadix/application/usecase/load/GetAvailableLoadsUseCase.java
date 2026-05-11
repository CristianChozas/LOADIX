package com.loadix.application.usecase.load;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.loadix.application.dto.response.LoadResponse;
import com.loadix.application.dto.response.MyLoadsPageResponse;
import com.loadix.application.port.in.GetAvailableLoadsPort;
import com.loadix.application.port.out.LoadPort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.model.UserAccount;
import com.loadix.domain.valueobject.UserRole;

public class GetAvailableLoadsUseCase implements GetAvailableLoadsPort {

    private static final int MAX_PAGE_SIZE = 10;

    private final UserAccountPort userAccountPort;
    private final LoadPort loadPort;

    public GetAvailableLoadsUseCase(UserAccountPort userAccountPort, LoadPort loadPort) {
        this.userAccountPort = userAccountPort;
        this.loadPort = loadPort;
    }

    @Override
    public MyLoadsPageResponse execute(String authenticatedEmail, int page, int size, boolean sortAsc) {
        UserAccount user = userAccountPort.findByEmail(normalizeEmail(authenticatedEmail))
            .orElseThrow(UserNotFoundException::new);

        if (user.role() != UserRole.CARRIER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only carrier users can view available loads");
        }

        int sanitizedPage = Math.max(page, 0);
        int sanitizedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        var result = loadPort.findAvailableLoads(sanitizedPage, sanitizedSize, sortAsc);

        List<LoadResponse> items = result.items().stream()
            .map(load -> new LoadResponse(
                load.id(),
                load.warehouseUserId(),
                load.createdByUserId(),
                new LoadResponse.LocationResponse(load.originAddress(), load.originCity(), load.originPostalCode()),
                new LoadResponse.LocationResponse(load.destinationAddress(), load.destinationCity(), load.destinationPostalCode()),
                load.cargoType(),
                load.weightKg(),
                load.loadQuantity(),
                load.loadUnitType(),
                load.pickupDate(),
                load.basePriceAmount(),
                load.notes(),
                load.specialRequirements(),
                load.status(),
                load.createdAt(),
                load.updatedAt()
            ))
            .toList();

        return new MyLoadsPageResponse(
            items,
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages(),
            result.hasNext(),
            result.hasPrevious()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
