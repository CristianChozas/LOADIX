package com.loadix.infrastructure.in.web.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.loadix.support.IntegrationTestContainers;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
class LoadControllerTest extends IntegrationTestContainers {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publishesLoadForWarehouseWithCompletedProfile() throws Exception {
        Cookie authCookie = registerAndLoginWarehouseUser("load-publish@loadix.test");
        createWarehouseProfile(authCookie);

        mockMvc.perform(post("/api/v1/loads")
                .cookie(authCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                    "\"origin\":{\"address\":\"Calle Uno 1\",\"city\":\"Madrid\",\"postalCode\":\"28001\"}," +
                    "\"destination\":{\"address\":\"Avenida Dos 2\",\"city\":\"Valencia\",\"postalCode\":\"46001\"}," +
                    "\"cargoType\":\"PALLETIZED\"," +
                    "\"weightKg\":1200.5," +
                    "\"pickupDate\":\"2099-01-10\"," +
                    "\"basePriceAmount\":850.0," +
                    "\"notes\":\"Muelle 3\"," +
                    "\"specialRequirements\":\"Transpaleta\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PUBLISHED"))
            .andExpect(jsonPath("$.origin.city").value("Madrid"))
            .andExpect(jsonPath("$.destination.postalCode").value("46001"))
            .andExpect(jsonPath("$.basePriceAmount").value(850.0));
    }

    @Test
    void rejectsLoadPublishForCarrierRole() throws Exception {
        Cookie authCookie = registerAndLoginCarrierUser("load-carrier@loadix.test");

        mockMvc.perform(post("/api/v1/loads")
                .cookie(authCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                    "\"origin\":{\"address\":\"Calle Uno 1\",\"city\":\"Madrid\",\"postalCode\":\"28001\"}," +
                    "\"destination\":{\"address\":\"Avenida Dos 2\",\"city\":\"Valencia\",\"postalCode\":\"46001\"}," +
                    "\"cargoType\":\"PALLETIZED\"," +
                    "\"weightKg\":1200.5," +
                    "\"pickupDate\":\"2099-01-10\"," +
                    "\"basePriceAmount\":850.0}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void rejectsLoadPublishWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/loads")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                    "\"origin\":{\"address\":\"Calle Uno 1\",\"city\":\"Madrid\",\"postalCode\":\"28001\"}," +
                    "\"destination\":{\"address\":\"Avenida Dos 2\",\"city\":\"Valencia\",\"postalCode\":\"46001\"}," +
                    "\"cargoType\":\"PALLETIZED\"," +
                    "\"weightKg\":1200.5," +
                    "\"pickupDate\":\"2099-01-10\"," +
                    "\"basePriceAmount\":850.0}"))
            .andExpect(status().isUnauthorized());
    }

    private void createWarehouseProfile(Cookie authCookie) throws Exception {
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
            .andExpect(status().isOk());
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
