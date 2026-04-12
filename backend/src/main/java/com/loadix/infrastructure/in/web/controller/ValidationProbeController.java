package com.loadix.infrastructure.in.web.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loadix.infrastructure.in.web.request.ValidationProbeRequest;
import com.loadix.infrastructure.in.web.response.ValidationProbeResponse;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/v1/contracts")
public class ValidationProbeController {

    @PostMapping("/validation-probe")
    public ValidationProbeResponse validateRequest(@Valid @RequestBody ValidationProbeRequest request) {
        return new ValidationProbeResponse(
                "Validation passed",
                request.name(),
                request.email());
    }
}
