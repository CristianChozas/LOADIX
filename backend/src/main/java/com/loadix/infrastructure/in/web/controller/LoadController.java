package com.loadix.infrastructure.in.web.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.loadix.application.dto.request.CreateLoadRequest;
import com.loadix.application.dto.request.UpdateLoadStatusRequest;
import com.loadix.application.dto.request.UpdateLoadRequest;
import com.loadix.application.dto.response.LoadResponse;
import com.loadix.application.dto.response.MyLoadsPageResponse;
import com.loadix.application.dto.response.WarehouseDashboardResponse;
import com.loadix.application.port.in.CreateLoadPort;
import com.loadix.application.port.in.GetAvailableLoadsPort;
import com.loadix.application.port.in.GetWarehouseDashboardMetricsPort;
import com.loadix.application.port.in.GetMyLoadsPort;
import com.loadix.application.port.in.UpdateMyLoadPort;
import com.loadix.application.port.in.UpdateMyLoadStatusPort;
import com.loadix.domain.model.AvailableLoadsFilters;
import com.loadix.domain.valueobject.CargoType;
import com.loadix.domain.exception.InvalidCredentialsException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/v1/loads")
@Tag(name = "Loads", description = "Load publication operations")
public class LoadController {

    private final CreateLoadPort createLoadPort;
    private final GetMyLoadsPort getMyLoadsPort;
    private final GetWarehouseDashboardMetricsPort getWarehouseDashboardMetricsPort;
    private final GetAvailableLoadsPort getAvailableLoadsPort;
    private final UpdateMyLoadPort updateMyLoadPort;
    private final UpdateMyLoadStatusPort updateMyLoadStatusPort;

    public LoadController(
        CreateLoadPort createLoadPort,
        GetMyLoadsPort getMyLoadsPort,
        GetWarehouseDashboardMetricsPort getWarehouseDashboardMetricsPort,
        GetAvailableLoadsPort getAvailableLoadsPort,
        UpdateMyLoadPort updateMyLoadPort,
        UpdateMyLoadStatusPort updateMyLoadStatusPort
    ) {
        this.createLoadPort = createLoadPort;
        this.getMyLoadsPort = getMyLoadsPort;
        this.getWarehouseDashboardMetricsPort = getWarehouseDashboardMetricsPort;
        this.getAvailableLoadsPort = getAvailableLoadsPort;
        this.updateMyLoadPort = updateMyLoadPort;
        this.updateMyLoadStatusPort = updateMyLoadStatusPort;
    }

    @PostMapping
    @Operation(summary = "Publish a new load")
    @ApiResponse(responseCode = "200", description = "Load published successfully",
        content = @Content(schema = @Schema(implementation = LoadResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid payload")
    @ApiResponse(responseCode = "401", description = "Unauthenticated")
    @ApiResponse(responseCode = "403", description = "Forbidden for current user")
    public LoadResponse createLoad(
        @Parameter(hidden = true) Authentication authentication,
        @Valid @RequestBody CreateLoadRequest request
    ) {
        if (authentication == null) {
            throw new InvalidCredentialsException();
        }

        return createLoadPort.execute(authentication.getName(), request);
    }

    @GetMapping("/mine")
    @Operation(summary = "List my load publications")
    @ApiResponse(responseCode = "200", description = "Loads retrieved successfully",
        content = @Content(schema = @Schema(implementation = MyLoadsPageResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid query params")
    @ApiResponse(responseCode = "401", description = "Unauthenticated")
    @ApiResponse(responseCode = "403", description = "Forbidden for current user")
    public MyLoadsPageResponse getMyLoads(
        @Parameter(hidden = true) Authentication authentication,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "desc") String sort,
        @RequestParam(required = false) LocalDate pickupDateFrom,
        @RequestParam(required = false) LocalDate pickupDateTo
    ) {
        if (authentication == null) {
            throw new InvalidCredentialsException();
        }

        boolean sortAsc = "asc".equalsIgnoreCase(sort);

        return getMyLoadsPort.execute(
            authentication.getName(),
            page,
            size,
            sortAsc,
            pickupDateFrom,
            pickupDateTo
        );
    }

    @GetMapping("/dashboard/warehouse")
    @Operation(summary = "Get warehouse dashboard metrics")
    @ApiResponse(responseCode = "200", description = "Dashboard metrics retrieved successfully",
        content = @Content(schema = @Schema(implementation = WarehouseDashboardResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthenticated")
    @ApiResponse(responseCode = "403", description = "Forbidden for current user")
    public WarehouseDashboardResponse getWarehouseDashboardMetrics(
        @Parameter(hidden = true) Authentication authentication
    ) {
        if (authentication == null) {
            throw new InvalidCredentialsException();
        }

        return getWarehouseDashboardMetricsPort.execute(authentication.getName());
    }

    @GetMapping("/available")
    @Operation(summary = "List available loads for carriers")
    @ApiResponse(responseCode = "200", description = "Available loads retrieved successfully",
        content = @Content(schema = @Schema(implementation = MyLoadsPageResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid query params")
    @ApiResponse(responseCode = "401", description = "Unauthenticated")
    @ApiResponse(responseCode = "403", description = "Forbidden for current user")
    public MyLoadsPageResponse getAvailableLoads(
        @Parameter(hidden = true) Authentication authentication,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "desc") String sort,
        @RequestParam(required = false) String query,
        @RequestParam(required = false) String origin,
        @RequestParam(required = false) String destination,
        @RequestParam(required = false) LocalDate pickupDate,
        @RequestParam(required = false) Integer palletsMin,
        @RequestParam(required = false) Integer palletsMax,
        @RequestParam(required = false) BigDecimal weightKgMin,
        @RequestParam(required = false) BigDecimal weightKgMax,
        @RequestParam(required = false) CargoType cargoType,
        @RequestParam(required = false) BigDecimal priceMin,
        @RequestParam(required = false) BigDecimal priceMax
    ) {
        if (authentication == null) {
            throw new InvalidCredentialsException();
        }

        boolean sortAsc = "asc".equalsIgnoreCase(sort);
        AvailableLoadsFilters filters = new AvailableLoadsFilters(
            query,
            origin,
            destination,
            pickupDate,
            palletsMin,
            palletsMax,
            weightKgMin,
            weightKgMax,
            cargoType,
            priceMin,
            priceMax
        );

        return getAvailableLoadsPort.execute(authentication.getName(), page, size, sortAsc, filters);
    }

    @PutMapping("/{loadId}")
    @Operation(summary = "Edit my published load")
    @ApiResponse(responseCode = "200", description = "Load updated successfully",
        content = @Content(schema = @Schema(implementation = LoadResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid payload")
    @ApiResponse(responseCode = "401", description = "Unauthenticated")
    @ApiResponse(responseCode = "403", description = "Forbidden for current user")
    @ApiResponse(responseCode = "404", description = "Load not found")
    public LoadResponse updateMyLoad(
        @Parameter(hidden = true) Authentication authentication,
        @PathVariable java.util.UUID loadId,
        @Valid @RequestBody UpdateLoadRequest request
    ) {
        if (authentication == null) {
            throw new InvalidCredentialsException();
        }

        return updateMyLoadPort.execute(authentication.getName(), loadId, request);
    }

    @PatchMapping("/{loadId}/status")
    @Operation(summary = "Update the operational status of my load")
    @ApiResponse(responseCode = "200", description = "Load status updated successfully",
        content = @Content(schema = @Schema(implementation = LoadResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid status transition")
    @ApiResponse(responseCode = "401", description = "Unauthenticated")
    @ApiResponse(responseCode = "403", description = "Forbidden for current user")
    @ApiResponse(responseCode = "404", description = "Load not found")
    public LoadResponse updateMyLoadStatus(
        @Parameter(hidden = true) Authentication authentication,
        @PathVariable java.util.UUID loadId,
        @Valid @RequestBody UpdateLoadStatusRequest request
    ) {
        if (authentication == null) {
            throw new InvalidCredentialsException();
        }

        return updateMyLoadStatusPort.execute(authentication.getName(), loadId, request);
    }
}
