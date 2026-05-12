package com.loadix.infrastructure.in.web.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.loadix.support.IntegrationTestContainers;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
class LoadControllerTest extends IntegrationTestContainers {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
                    "\"loadQuantity\":24," +
                    "\"loadUnitType\":\"PALLETS\"," +
                    "\"pickupDate\":\"2099-01-10\"," +
                    "\"basePriceAmount\":850.0," +
                    "\"notes\":\"Muelle 3\"," +
                    "\"specialRequirements\":\"Transpaleta\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PUBLISHED"))
            .andExpect(jsonPath("$.origin.city").value("Madrid"))
            .andExpect(jsonPath("$.destination.postalCode").value("46001"))
            .andExpect(jsonPath("$.loadQuantity").value(24))
            .andExpect(jsonPath("$.loadUnitType").value("PALLETS"))
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
                    "\"loadQuantity\":12," +
                    "\"loadUnitType\":\"PALLETS\"," +
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
                    "\"loadQuantity\":12," +
                    "\"loadUnitType\":\"PALLETS\"," +
                    "\"pickupDate\":\"2099-01-10\"," +
                    "\"basePriceAmount\":850.0}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void listsMyLoadsPaginatedWithMostRecentFirst() throws Exception {
        Cookie authCookie = registerAndLoginWarehouseUser("load-list@loadix.test");
        createWarehouseProfile(authCookie);

        publishLoad(authCookie, "Madrid", "2099-01-10", 850.0);
        publishLoad(authCookie, "Sevilla", "2099-02-12", 910.0);

        mockMvc.perform(get("/api/v1/loads/mine")
                .cookie(authCookie)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.items[0].origin.city").value("Sevilla"))
            .andExpect(jsonPath("$.items[1].origin.city").value("Madrid"));
    }

    @Test
    void filtersMyLoadsByPickupDateRange() throws Exception {
        Cookie authCookie = registerAndLoginWarehouseUser("load-range@loadix.test");
        createWarehouseProfile(authCookie);

        publishLoad(authCookie, "Bilbao", "2099-01-05", 700.0);
        publishLoad(authCookie, "Zaragoza", "2099-03-15", 880.0);

        mockMvc.perform(get("/api/v1/loads/mine")
                .cookie(authCookie)
                .param("pickupDateFrom", "2099-03-01")
                .param("pickupDateTo", "2099-03-31"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.items[0].origin.city").value("Zaragoza"));
    }

    @Test
    void updatesMyPublishedLoad() throws Exception {
        Cookie authCookie = registerAndLoginWarehouseUser("load-update@loadix.test");
        createWarehouseProfile(authCookie);

        MvcResult created = publishLoad(authCookie, "Madrid", "2099-05-10", 850.0);
        JsonNode body = objectMapper.readTree(created.getResponse().getContentAsString());
        String loadId = body.get("id").asText();

        mockMvc.perform(put("/api/v1/loads/{id}", loadId)
                .cookie(authCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                    "\"origin\":{\"address\":\"Calle Nueva 9\",\"city\":\"Barcelona\",\"postalCode\":\"08001\"}," +
                    "\"destination\":{\"address\":\"Avenida Dos 2\",\"city\":\"Valencia\",\"postalCode\":\"46001\"}," +
                    "\"cargoType\":\"PALLETIZED\"," +
                    "\"weightKg\":1300.0," +
                    "\"loadQuantity\":18," +
                    "\"loadUnitType\":\"PALLETS\"," +
                    "\"pickupDate\":\"2099-05-12\"," +
                    "\"basePriceAmount\":920.0," +
                    "\"notes\":\"Actualizada\"," +
                    "\"specialRequirements\":\"Nada\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.origin.city").value("Barcelona"))
            .andExpect(jsonPath("$.basePriceAmount").value(920.0));
    }

    @Test
    void listsAvailableLoadsForCarrier() throws Exception {
        Cookie warehouseCookie = registerAndLoginWarehouseUser("load-available-warehouse@loadix.test");
        createWarehouseProfile(warehouseCookie);
        publishLoad(warehouseCookie, "Madrid", "Valencia", "PALLETIZED", "2099-06-01", 10, 1200.5, 700.0);
        publishLoad(warehouseCookie, "Bilbao", "Sevilla", "PALLETIZED", "2099-06-10", 10, 1200.5, 900.0);

        Cookie carrierCookie = registerAndLoginCarrierUser("load-available-carrier@loadix.test");

        mockMvc.perform(get("/api/v1/loads/available")
                .cookie(carrierCookie)
                .param("page", "0")
                .param("size", "2")
                .param("sort", "desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].origin.city").value("Bilbao"))
            .andExpect(jsonPath("$.items[1].origin.city").value("Madrid"))
            .andExpect(jsonPath("$.items[0].loadQuantity").value(10))
            .andExpect(jsonPath("$.items[0].loadUnitType").value("PALLETS"));
    }

    @Test
    void filtersAvailableLoadsForCarrierBySearchAndStructuredFilters() throws Exception {
        Cookie warehouseCookie = registerAndLoginWarehouseUser("load-available-filter-warehouse@loadix.test");
        createWarehouseProfile(warehouseCookie);

        publishLoad(warehouseCookie, "Madrid", "Sevilla", "PALLETIZED", "2099-07-01", 12, 1200.0, 900.0);
        publishLoad(warehouseCookie, "Barcelona", "Valencia", "REFRIGERATED", "2099-07-15", 8, 800.0, 1200.0);

        Cookie carrierCookie = registerAndLoginCarrierUser("load-available-filter-carrier@loadix.test");

        mockMvc.perform(get("/api/v1/loads/available")
                .cookie(carrierCookie)
                .param("query", "valen")
                .param("destination", "Valencia")
                .param("pickupDate", "2099-07-15")
                .param("palletsMin", "6")
                .param("palletsMax", "10")
                .param("weightKgMin", "700")
                .param("weightKgMax", "900")
                .param("cargoType", "REFRIGERATED")
                .param("priceMin", "1000")
                .param("priceMax", "1300"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.items[0].origin.city").value("Barcelona"))
            .andExpect(jsonPath("$.items[0].destination.city").value("Valencia"))
            .andExpect(jsonPath("$.items[0].cargoType").value("REFRIGERATED"));
    }

    @Test
    void rejectsAvailableLoadsForWarehouseRole() throws Exception {
        Cookie warehouseCookie = registerAndLoginWarehouseUser("load-available-forbidden@loadix.test");

        mockMvc.perform(get("/api/v1/loads/available")
                .cookie(warehouseCookie)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isForbidden());
    }

    @Test
    void updatesLoadStatusFromPublishedToReserved() throws Exception {
        Cookie authCookie = registerAndLoginWarehouseUser("load-status-update@loadix.test");
        createWarehouseProfile(authCookie);

        MvcResult created = publishLoad(authCookie, "Madrid", "2099-08-10", 990.0);
        String loadId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(patch("/api/v1/loads/{id}/status", loadId)
                .cookie(authCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                    "\"status\":\"RESERVED\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("RESERVED"));
    }

    @Test
    void rejectsInvalidLoadStatusTransition() throws Exception {
        Cookie authCookie = registerAndLoginWarehouseUser("load-status-invalid@loadix.test");
        createWarehouseProfile(authCookie);

        MvcResult created = publishLoad(authCookie, "Madrid", "2099-09-10", 990.0);
        String loadId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(patch("/api/v1/loads/{id}/status", loadId)
                .cookie(authCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                    "\"status\":\"DELIVERED\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsLoadStatusUpdateForCarrierRole() throws Exception {
        Cookie warehouseCookie = registerAndLoginWarehouseUser("load-status-owner@loadix.test");
        createWarehouseProfile(warehouseCookie);
        MvcResult created = publishLoad(warehouseCookie, "Madrid", "2099-10-10", 990.0);
        String loadId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        Cookie carrierCookie = registerAndLoginCarrierUser("load-status-carrier@loadix.test");

        mockMvc.perform(patch("/api/v1/loads/{id}/status", loadId)
                .cookie(carrierCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                    "\"status\":\"RESERVED\"}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void returnsWarehouseDashboardMetricsWithWeeklyAndStatusDistribution() throws Exception {
        Cookie authCookie = registerAndLoginWarehouseUser("load-dashboard@loadix.test");
        createWarehouseProfile(authCookie);

        publishLoad(authCookie, "Madrid", "2099-12-10", 850.0);
        MvcResult secondLoad = publishLoad(authCookie, "Sevilla", "2099-12-12", 910.0);
        String secondLoadId = objectMapper.readTree(secondLoad.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(patch("/api/v1/loads/{id}/status", secondLoadId)
                .cookie(authCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                    "\"status\":\"RESERVED\"}"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/loads/dashboard/warehouse")
                .cookie(authCookie))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.monthlyLoads").isNumber())
            .andExpect(jsonPath("$.weeklyActivity.length()").value(7))
            .andExpect(jsonPath("$.statusDistribution.length()").value(5))
            .andExpect(jsonPath("$.statusDistribution[0].status").value("PUBLISHED"))
            .andExpect(jsonPath("$.statusDistribution[1].status").value("RESERVED"))
            .andExpect(jsonPath("$.statusDistribution[0].count").value(1))
            .andExpect(jsonPath("$.statusDistribution[1].count").value(1));
    }

    @Test
    void returnsCarrierDashboardMetricsWithWeeklyAndCargoTypeDistribution() throws Exception {
        Cookie carrierCookie = registerAndLoginCarrierUser("carrier-dashboard@loadix.test");

        MvcResult baselineDashboard = mockMvc.perform(get("/api/v1/loads/dashboard/carrier")
                .cookie(carrierCookie))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode baselinePayload = objectMapper.readTree(baselineDashboard.getResponse().getContentAsString());
        long baselineAvailableLoads = baselinePayload.get("availableLoads").asLong();
        long baselinePalletizedCount = baselinePayload.get("cargoTypeDistribution").get(0).get("count").asLong();
        long baselineRefrigeratedCount = baselinePayload.get("cargoTypeDistribution").get(2).get("count").asLong();

        Cookie warehouseCookie = registerAndLoginWarehouseUser("carrier-dashboard-warehouse@loadix.test");
        createWarehouseProfile(warehouseCookie);

        publishLoad(warehouseCookie, "Madrid", "Valencia", "PALLETIZED", "2099-12-10", 10, 1200.5, 850.0);
        publishLoad(warehouseCookie, "Sevilla", "Bilbao", "REFRIGERATED", "2099-12-12", 8, 950.0, 910.0);

        mockMvc.perform(get("/api/v1/loads/dashboard/carrier")
                .cookie(carrierCookie))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.availableLoads").value(baselineAvailableLoads + 2))
            .andExpect(jsonPath("$.weeklyActivity.length()").value(7))
            .andExpect(jsonPath("$.cargoTypeDistribution.length()").value(9))
            .andExpect(jsonPath("$.cargoTypeDistribution[0].cargoType").value("PALLETIZED"))
            .andExpect(jsonPath("$.cargoTypeDistribution[0].count").value(baselinePalletizedCount + 1))
            .andExpect(jsonPath("$.cargoTypeDistribution[2].cargoType").value("REFRIGERATED"))
            .andExpect(jsonPath("$.cargoTypeDistribution[2].count").value(baselineRefrigeratedCount + 1));
    }

    @Test
    void rejectsCarrierDashboardForWarehouseRole() throws Exception {
        Cookie authCookie = registerAndLoginWarehouseUser("carrier-dashboard-forbidden@loadix.test");

        mockMvc.perform(get("/api/v1/loads/dashboard/carrier")
                .cookie(authCookie))
            .andExpect(status().isForbidden());
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

    private MvcResult publishLoad(Cookie authCookie, String originCity, String pickupDate, double basePriceAmount) throws Exception {
        return publishLoad(authCookie, originCity, "Valencia", "PALLETIZED", pickupDate, 10, 1200.5, basePriceAmount);
    }

    private MvcResult publishLoad(
        Cookie authCookie,
        String originCity,
        String destinationCity,
        String cargoType,
        String pickupDate,
        int loadQuantity,
        double weightKg,
        double basePriceAmount
    ) throws Exception {
        return mockMvc.perform(post("/api/v1/loads")
                .cookie(authCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                    "\"origin\":{\"address\":\"Calle Uno 1\",\"city\":\"" + originCity + "\",\"postalCode\":\"28001\"}," +
                    "\"destination\":{\"address\":\"Avenida Dos 2\",\"city\":\"" + destinationCity + "\",\"postalCode\":\"46001\"}," +
                    "\"cargoType\":\"" + cargoType + "\"," +
                    "\"weightKg\":" + weightKg + "," +
                    "\"loadQuantity\":" + loadQuantity + "," +
                    "\"loadUnitType\":\"PALLETS\"," +
                    "\"pickupDate\":\"" + pickupDate + "\"," +
                    "\"basePriceAmount\":" + basePriceAmount + "}"))
            .andExpect(status().isOk())
            .andReturn();
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
