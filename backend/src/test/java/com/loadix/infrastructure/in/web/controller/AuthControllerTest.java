package com.loadix.infrastructure.in.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
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
                                .andExpect(jsonPath("$.email").value("register@loadix.test"))
                                .andExpect(header().doesNotExist("Set-Cookie"));

                var user = userJpaRepository.findByEmailIgnoreCase("register@loadix.test").orElseThrow();
                assertThat(user.getPasswordHash()).isNotEqualTo("Password1");
        }

        @Test
        void logsInSetsHttpOnlyCookie() throws Exception {
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{" +
                                                "\"email\":\"login@loadix.test\"," +
                                                "\"password\":\"Password1\"," +
                                                "\"role\":\"WAREHOUSE\"}"))
                                .andExpect(status().isCreated());

                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{" +
                                                "\"email\":\"login@loadix.test\"," +
                                                "\"password\":\"Password1\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("login@loadix.test"))
                                .andExpect(jsonPath("$.role").value("WAREHOUSE"))
                                .andExpect(header().string("Set-Cookie", containsString("LOADIX_AUTH=")))
                                .andExpect(header().string("Set-Cookie", containsString("HttpOnly")));
        }

        @Test
        void meReturnsCurrentUserWhenCookieIsPresent() throws Exception {
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{" +
                                                "\"email\":\"me@loadix.test\"," +
                                                "\"password\":\"Password1\"," +
                                                "\"role\":\"CARRIER\"}"))
                                .andExpect(status().isCreated());

                MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{" +
                                                "\"email\":\"me@loadix.test\"," +
                                                "\"password\":\"Password1\"}"))
                                .andExpect(status().isOk())
                                .andReturn();

                Cookie authCookie = loginResult.getResponse().getCookie("LOADIX_AUTH");

                mockMvc.perform(get("/api/v1/auth/me")
                                .cookie(authCookie))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("me@loadix.test"))
                                .andExpect(jsonPath("$.role").value("CARRIER"));
        }

        @Test
        void logoutClearsAuthCookie() throws Exception {
                mockMvc.perform(post("/api/v1/auth/logout"))
                                .andExpect(status().isOk())
                                .andExpect(header().string("Set-Cookie", containsString("LOADIX_AUTH=")))
                                .andExpect(header().string("Set-Cookie", containsString("Max-Age=0")));
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
