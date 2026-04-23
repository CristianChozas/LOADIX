package com.loadix.infrastructure.in.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loadix.application.dto.request.CreateCarrierProfileRequest;
import com.loadix.application.dto.request.CreateWarehouseProfileRequest;
import com.loadix.application.dto.request.UpdateWarehouseProfileRequest;
import com.loadix.application.dto.response.CarrierProfileResponse;
import com.loadix.application.dto.response.WarehouseProfileResponse;
import com.loadix.application.port.in.CreateCarrierProfilePort;
import com.loadix.application.port.in.CreateWarehouseProfilePort;
import com.loadix.application.port.in.GetWarehouseProfilePort;
import com.loadix.application.port.in.UpdateWarehouseProfilePort;
import com.loadix.domain.exception.InvalidCredentialsException;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/api/v1/profiles")
@Tag(name = "Profiles", description = "Warehouse and carrier profile operations")
public class ProfileController {

    private final CreateWarehouseProfilePort createWarehouseProfilePort;
    private final GetWarehouseProfilePort getWarehouseProfilePort;
    private final UpdateWarehouseProfilePort updateWarehouseProfilePort;
    private final CreateCarrierProfilePort createCarrierProfilePort;

    public ProfileController(
        CreateWarehouseProfilePort createWarehouseProfilePort,
        GetWarehouseProfilePort getWarehouseProfilePort,
        UpdateWarehouseProfilePort updateWarehouseProfilePort,
        CreateCarrierProfilePort createCarrierProfilePort) {
            this.createWarehouseProfilePort = createWarehouseProfilePort;
            this.getWarehouseProfilePort = getWarehouseProfilePort;
            this.updateWarehouseProfilePort = updateWarehouseProfilePort;
            this.createCarrierProfilePort = createCarrierProfilePort;
    }

    @PostMapping("/warehouse")
    @Operation(summary = "Create the authenticated warehouse profile")
    @ApiResponse(responseCode = "200", description = "Warehouse profile created or updated",
            content = @Content(schema = @Schema(implementation = WarehouseProfileResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid warehouse profile payload")
    @ApiResponse(responseCode = "401", description = "Unauthenticated")
    public WarehouseProfileResponse createWarehouseProfile(
        @Parameter(hidden = true) Authentication authentication,
        @Valid @RequestBody CreateWarehouseProfileRequest request
    ) {
        if (authentication == null) {
            throw new InvalidCredentialsException();
        }

        return createWarehouseProfilePort.execute(authentication.getName(), request);
    }

    @GetMapping("/warehouse")
    @Operation(summary = "Get the authenticated warehouse profile")
    @ApiResponse(responseCode = "200", description = "Warehouse profile returned",
            content = @Content(schema = @Schema(implementation = WarehouseProfileResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthenticated")
    public WarehouseProfileResponse getWarehouseProfile(
        @Parameter(hidden = true) Authentication authentication
    ) {
        if (authentication == null) {
            throw new InvalidCredentialsException();
        }

        return getWarehouseProfilePort.execute(authentication.getName());
    }

    @PutMapping("/warehouse")
    @Operation(summary = "Update the authenticated warehouse profile")
    @ApiResponse(responseCode = "200", description = "Warehouse profile updated",
            content = @Content(schema = @Schema(implementation = WarehouseProfileResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid warehouse profile payload")
    @ApiResponse(responseCode = "401", description = "Unauthenticated")
    public WarehouseProfileResponse updateWarehouseProfile(
        @Parameter(hidden = true) Authentication authentication,
        @Valid @RequestBody UpdateWarehouseProfileRequest request
    ) {
        if (authentication == null) {
            throw new InvalidCredentialsException();
        }

        return updateWarehouseProfilePort.execute(authentication.getName(), request);
    }

    @PostMapping("/carrier")
    @Operation(summary = "Create the authenticated carrier profile")
    @ApiResponse(responseCode = "200", description = "Carrier profile created",
            content = @Content(schema = @Schema(implementation = CarrierProfileResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid carrier profile payload")
    @ApiResponse(responseCode = "401", description = "Unauthenticated")
    public CarrierProfileResponse createCarrierProfile(
        @Parameter(hidden = true) Authentication authentication,
        @Valid @RequestBody CreateCarrierProfileRequest request
    ) {
        if (authentication == null) {
            throw new InvalidCredentialsException();
        }

        return createCarrierProfilePort.execute(authentication.getName(), request);
    }
}
