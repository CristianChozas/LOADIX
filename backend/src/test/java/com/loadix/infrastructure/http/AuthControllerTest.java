package com.loadix.infrastructure.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.loadix.infrastructure.persistence.repository.UserJpaRepository;
import com.loadix.support.IntegrationTestContainers;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest extends IntegrationTestContainers {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void cleanDatabase() {
        userJpaRepository.deleteAll();
    }

    @Test
    void registersUser() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\":\"register@loadix.test\"," +
                                "\"password\":\"Password1\"," +
                                "\"role\":\"WAREHOUSE\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("register@loadix.test"))
                .andExpect(header().doesNotExist("Set-Cookie"));

        var user = userJpaRepository.findByEmailIgnoreCase("register@loadix.test").orElseThrow();
        assertThat(user.getPasswordHash()).isNotEqualTo("Password1");
    }

    @Test
    void rejectsDuplicateRegistrationEmail() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\":\"duplicate@loadix.test\"," +
                                "\"password\":\"Password1\"," +
                                "\"role\":\"CARRIER\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\":\"duplicate@loadix.test\"," +
                                "\"password\":\"Password1\"," +
                                "\"role\":\"CARRIER\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }
}
