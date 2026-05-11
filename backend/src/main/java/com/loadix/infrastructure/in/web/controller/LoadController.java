package com.loadix.infrastructure.in.web.controller;

import java.time.LocalDate;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.loadix.application.dto.request.CreateLoadRequest;
import com.loadix.application.dto.request.UpdateLoadRequest;
import com.loadix.application.dto.response.LoadResponse;
import com.loadix.application.dto.response.MyLoadsPageResponse;
import com.loadix.application.port.in.CreateLoadPort;
import com.loadix.application.port.in.GetAvailableLoadsPort;
import com.loadix.application.port.in.GetMyLoadsPort;
import com.loadix.application.port.in.UpdateMyLoadPort;
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
    private final GetAvailableLoadsPort getAvailableLoadsPort;
    private final UpdateMyLoadPort updateMyLoadPort;

    public LoadController(
        CreateLoadPort createLoadPort,
        GetMyLoadsPort getMyLoadsPort,
        GetAvailableLoadsPort getAvailableLoadsPort,
        UpdateMyLoadPort updateMyLoadPort
    ) {
        this.createLoadPort = createLoadPort;
        this.getMyLoadsPort = getMyLoadsPort;
        this.getAvailableLoadsPort = getAvailableLoadsPort;
        this.updateMyLoadPort = updateMyLoadPort;
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
        @RequestParam(defaultValue = "desc") String sort
    ) {
        if (authentication == null) {
            throw new InvalidCredentialsException();
        }

        boolean sortAsc = "asc".equalsIgnoreCase(sort);
        return getAvailableLoadsPort.execute(authentication.getName(), page, size, sortAsc);
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
}
