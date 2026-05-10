package com.loadix.infrastructure.in.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loadix.infrastructure.in.web.response.HealthResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health", description = "Service health checks")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Get service health")
    @ApiResponse(responseCode = "200", description = "Service is healthy",
            content = @Content(schema = @Schema(implementation = HealthResponse.class)))
    public HealthResponse health() {
        return new HealthResponse("ok", "loadix-backend-api", "0.0.1-SNAPSHOT");
    }
}
