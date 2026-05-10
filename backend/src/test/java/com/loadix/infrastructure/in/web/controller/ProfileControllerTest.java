package com.loadix.infrastructure.in.web.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.loadix.support.IntegrationTestContainers;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerTest extends IntegrationTestContainers {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void createsWarehouseProfileForAuthenticatedUser() throws Exception {
                Cookie authCookie = registerAndLoginWarehouseUser("create-profile@loadix.test");

                mockMvc.perform(post("/api/v1/profiles/warehouse")
                                .cookie(authCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{" +
                                                "\"companyName\":\"Almacenes Norte\"," +
                                                "\"cif\":\"B12345678\"," +
                                                "\"address\":\"Calle Logistica 1\"," +
                                                "\"postalCode\":\"28001\"," +
                                                "\"city\":\"Madrid\"," +
                                                "\"phone\":\"600000000\"," +
                                                "\"contactPerson\":\"Ana Lopez\"," +
                                                "\"cargoType\":\"PALLETIZED\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.companyName").value("Almacenes Norte"))
                                .andExpect(jsonPath("$.postalCode").value("28001"))
                                .andExpect(jsonPath("$.contactPerson").value("Ana Lopez"))
                                .andExpect(jsonPath("$.cargoType").value("PALLETIZED"))
                                .andExpect(jsonPath("$.completed").value(true));
        }

        @Test
        void returnsWarehouseProfileForAuthenticatedUser() throws Exception {
                Cookie authCookie = registerAndLoginWarehouseUser("get-profile@loadix.test");

                mockMvc.perform(post("/api/v1/profiles/warehouse")
                                .cookie(authCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{" +
                                                "\"companyName\":\"Depot Central\"," +
                                                "\"cif\":\"B87654321\"," +
                                                "\"address\":\"Avenida Puerto 5\"," +
                                                "\"postalCode\":\"41001\"," +
                                                "\"city\":\"Sevilla\"," +
                                                "\"phone\":\"611111111\"," +
                                                "\"contactPerson\":\"Luis Garcia\"," +
                                                "\"cargoType\":\"GENERAL\"}"))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/api/v1/profiles/warehouse")
                                .cookie(authCookie))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.companyName").value("Depot Central"))
                                .andExpect(jsonPath("$.city").value("Sevilla"))
                                .andExpect(jsonPath("$.cargoType").value("GENERAL"))
                                .andExpect(jsonPath("$.completed").value(true));
        }

        @Test
        void updatesWarehouseProfileForAuthenticatedUser() throws Exception {
                Cookie authCookie = registerAndLoginWarehouseUser("update-profile@loadix.test");

                mockMvc.perform(post("/api/v1/profiles/warehouse")
                                .cookie(authCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{" +
                                                "\"companyName\":\"Update Depot\"," +
                                                "\"cif\":\"B99999999\"," +
                                                "\"address\":\"Calle Uno\"," +
                                                "\"postalCode\":\"08001\"," +
                                                "\"city\":\"Barcelona\"," +
                                                "\"phone\":\"622222222\"," +
                                                "\"contactPerson\":\"Marta Ruiz\"," +
                                                "\"cargoType\":\"BULK\"}"))
                                .andExpect(status().isOk());

                mockMvc.perform(put("/api/v1/profiles/warehouse")
                                .cookie(authCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{" +
                                                "\"companyName\":\"Update Depot\"," +
                                                "\"cif\":\"B99999999\"," +
                                                "\"address\":\"Calle Dos\"," +
                                                "\"postalCode\":\"08002\"," +
                                                "\"city\":\"Barcelona\"," +
                                                "\"phone\":\"699999999\"," +
                                                "\"contactPerson\":\"Marta Ruiz\"," +
                                                "\"cargoType\":\"BULK\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.address").value("Calle Dos"))
                                .andExpect(jsonPath("$.postalCode").value("08002"))
                                .andExpect(jsonPath("$.phone").value("699999999"))
                                .andExpect(jsonPath("$.completed").value(true));
        }

        @Test
        void createsCarrierProfileForAuthenticatedUser() throws Exception {
                Cookie authCookie = registerAndLoginCarrierUser("carrier-profile@loadix.test");

                mockMvc.perform(post("/api/v1/profiles/carrier")
                                .cookie(authCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{" +
                                                "\"name\":\"Carlos\"," +
                                                "\"lastName\":\"Gomez\"," +
                                                "\"phone\":\"612345678\"," +
                                                "\"vehicleType\":\"FURGONETA\"," +
                                                "\"licensePlate\":\"1234ABC\"," +
                                                "\"carnet\":\"C1\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Carlos"))
                                .andExpect(jsonPath("$.lastName").value("Gomez"))
                                .andExpect(jsonPath("$.vehicleType").value("FURGONETA"))
                                .andExpect(jsonPath("$.licensePlate").value("1234ABC"))
                                .andExpect(jsonPath("$.completed").value(true));
        }

        @Test
        void rejectsInvalidCarrierProfilePayload() throws Exception {
                Cookie authCookie = registerAndLoginCarrierUser("carrier-invalid@loadix.test");

                mockMvc.perform(post("/api/v1/profiles/carrier")
                                .cookie(authCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{" +
                                                "\"name\":\"\"," +
                                                "\"lastName\":\"Gomez\"," +
                                                "\"phone\":\"612345678\"," +
                                                "\"vehicleType\":\"FURGONETA\"," +
                                                "\"licensePlate\":\"1234ABC\"," +
                                                "\"carnet\":\"C1\"}"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void returnsCarrierProfileForAuthenticatedUser() throws Exception {
                Cookie authCookie = registerAndLoginCarrierUser("carrier-get-profile@loadix.test");

                mockMvc.perform(post("/api/v1/profiles/carrier")
                                .cookie(authCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{" +
                                                "\"name\":\"Laura\"," +
                                                "\"lastName\":\"Martin\"," +
                                                "\"phone\":\"612345678\"," +
                                                "\"vehicleType\":\"TRAILER\"," +
                                                "\"licensePlate\":\"7777XYZ\"," +
                                                "\"carnet\":\"C+E\"}"))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/api/v1/profiles/carrier")
                                .cookie(authCookie))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Laura"))
                                .andExpect(jsonPath("$.lastName").value("Martin"))
                                .andExpect(jsonPath("$.vehicleType").value("TRAILER"))
                                .andExpect(jsonPath("$.licensePlate").value("7777XYZ"))
                                .andExpect(jsonPath("$.completed").value(true));
        }

        @Test
        void returnsNotFoundWhenCarrierProfileDoesNotExist() throws Exception {
                Cookie authCookie = registerAndLoginCarrierUser("carrier-without-profile@loadix.test");

                mockMvc.perform(get("/api/v1/profiles/carrier")
                                .cookie(authCookie))
                                .andExpect(status().isNotFound());
        }

        @Test
        void rejectsWarehouseProfileWithoutAuthentication() throws Exception {
                mockMvc.perform(get("/api/v1/profiles/warehouse"))
                                .andExpect(status().isUnauthorized());
        }

        private Cookie registerAndLoginWarehouseUser(String email) throws Exception {
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{" +
                                                "\"email\":\"" + email + "\"," +
                                                "\"password\":\"Password1\"," +
                                                "\"role\":\"WAREHOUSE\"}"))
                                .andExpect(status().isCreated());

                MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{" +
                                                "\"email\":\"" + email + "\"," +
                                                "\"password\":\"Password1\"}"))
                                .andExpect(status().isOk())
                                .andExpect(header().string("Set-Cookie", containsString("LOADIX_AUTH=")))
                                .andReturn();

                return loginResult.getResponse().getCookie("LOADIX_AUTH");
        }

        private Cookie registerAndLoginCarrierUser(String email) throws Exception {
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{" +
                                                "\"email\":\"" + email + "\"," +
                                                "\"password\":\"Password1\"," +
                                                "\"role\":\"CARRIER\"}"))
                                .andExpect(status().isCreated());

                MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{" +
                                                "\"email\":\"" + email + "\"," +
                                                "\"password\":\"Password1\"}"))
                                .andExpect(status().isOk())
                                .andExpect(header().string("Set-Cookie", containsString("LOADIX_AUTH=")))
                                .andReturn();

                return loginResult.getResponse().getCookie("LOADIX_AUTH");
        }
}
