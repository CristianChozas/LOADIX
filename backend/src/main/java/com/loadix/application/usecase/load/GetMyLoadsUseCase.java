package com.loadix.application.usecase.load;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.loadix.application.dto.response.LoadResponse;
import com.loadix.application.dto.response.MyLoadsPageResponse;
import com.loadix.application.port.in.GetMyLoadsPort;
import com.loadix.application.port.out.LoadPort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.model.UserAccount;
import com.loadix.domain.valueobject.UserRole;

public class GetMyLoadsUseCase implements GetMyLoadsPort {

    private static final int MAX_PAGE_SIZE = 10;

    private final UserAccountPort userAccountPort;
    private final LoadPort loadPort;

    public GetMyLoadsUseCase(UserAccountPort userAccountPort, LoadPort loadPort) {
        this.userAccountPort = userAccountPort;
        this.loadPort = loadPort;
    }

    @Override
    public MyLoadsPageResponse execute(
        String authenticatedEmail,
        int page,
        int size,
        boolean sortAsc,
        LocalDate pickupDateFrom,
        LocalDate pickupDateTo
    ) {
        UserAccount user = userAccountPort.findByEmail(normalizeEmail(authenticatedEmail))
            .orElseThrow(UserNotFoundException::new);

        if (user.role() != UserRole.WAREHOUSE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only warehouse users can view published loads");
        }

        if (pickupDateFrom != null && pickupDateTo != null && pickupDateFrom.isAfter(pickupDateTo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pickupDateFrom must be before or equal to pickupDateTo");
        }

        int sanitizedPage = Math.max(page, 0);
        int sanitizedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        var result = loadPort.findByWarehouseUserId(
            user.id(),
            sanitizedPage,
            sanitizedSize,
            sortAsc,
            pickupDateFrom,
            pickupDateTo
        );

        List<LoadResponse> items = result.items().stream()
            .map(load -> new LoadResponse(
                load.id(),
                load.warehouseUserId(),
                load.createdByUserId(),
                new LoadResponse.LocationResponse(load.originAddress(), load.originCity(), load.originPostalCode()),
                new LoadResponse.LocationResponse(load.destinationAddress(), load.destinationCity(), load.destinationPostalCode()),
                load.cargoType(),
                load.weightKg(),
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
