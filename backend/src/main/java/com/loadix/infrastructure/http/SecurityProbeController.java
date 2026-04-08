package com.loadix.infrastructure.http;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/security")
public class SecurityProbeController {

    @GetMapping("/context")
    public Map<String, Object> context(Authentication authentication) {
        return Map.of(
                "authenticated", true,
                "principal", authentication.getName(),
                "authorities", authentication.getAuthorities().stream().map(Object::toString).toList()
        );
    }
}
