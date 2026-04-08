package com.loadix.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.loadix.infrastructure.config.SecurityProperties;

class JwtTokenServiceTest {

    @Test
    void createsAndParsesJwtClaims() {
        SecurityProperties properties = new SecurityProperties(
                List.of("http://localhost:3000"),
                "change-me-to-a-long-random-secret-with-at-least-32-characters",
                Duration.ofHours(24)
        );
        JwtTokenService service = new JwtTokenService(properties);

        String token = service.createToken("developer@loadix.test", List.of("WAREHOUSE"));

        assertThat(service.parse(token).getSubject()).isEqualTo("developer@loadix.test");
        assertThat(service.parse(token).get("roles", List.class)).contains("WAREHOUSE");
    }
}
