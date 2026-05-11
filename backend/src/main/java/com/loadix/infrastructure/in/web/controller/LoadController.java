package com.loadix.infrastructure.in.web.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.loadix.application.dto.request.CreateLoadRequest;
import com.loadix.application.dto.response.LoadResponse;
import com.loadix.application.port.in.CreateLoadPort;
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

    public LoadController(CreateLoadPort createLoadPort) {
        this.createLoadPort = createLoadPort;
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
}
