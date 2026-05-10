package com.loadix.infrastructure.in.web.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loadix.infrastructure.in.web.request.ValidationProbeRequest;
import com.loadix.infrastructure.in.web.response.ValidationProbeResponse;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/api/v1/contracts")
@Tag(name = "Contracts", description = "Validation and contract probes")
public class ValidationProbeController {

    @PostMapping("/validation-probe")
    @Operation(summary = "Validate a sample request payload")
    @ApiResponse(responseCode = "200", description = "Validation passed",
            content = @Content(schema = @Schema(implementation = ValidationProbeResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validation failed")
    public ValidationProbeResponse validateRequest(@Valid @RequestBody ValidationProbeRequest request) {
        return new ValidationProbeResponse(
                "Validation passed",
                request.name(),
                request.email());
    }
}
