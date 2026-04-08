package com.loadix.infrastructure.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import com.loadix.infrastructure.security.JwtTokenService;
import com.loadix.support.IntegrationTestContainers;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityProbeControllerTest extends IntegrationTestContainers {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Test
    void rejectsProtectedEndpointWithoutJwt() throws Exception {
        mockMvc.perform(get("/api/v1/security/context"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void allowsProtectedEndpointWithJwt() throws Exception {
        String token = jwtTokenService.createToken("developer@loadix.test", java.util.List.of("WAREHOUSE"));

        mockMvc.perform(get("/api/v1/security/context")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.principal").value("developer@loadix.test"));
    }
}
