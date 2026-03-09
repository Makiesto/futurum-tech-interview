package com.example.backend.controller;

import com.example.backend.dto.CampaignRequest;
import com.example.backend.dto.CampaignResponse;
import com.example.backend.service.CampaignService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CampaignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CampaignService campaignService;

    private CampaignResponse campaignResponse;
    private CampaignRequest campaignRequest;
    private UUID campaignId;

    @BeforeEach
    void setUp() {
        campaignId = UUID.randomUUID();

        campaignResponse = new CampaignResponse();
        campaignResponse.setId(campaignId);
        campaignResponse.setName("Test Campaign");
        campaignResponse.setKeywords(List.of("sport", "shoes"));
        campaignResponse.setBidAmount(new BigDecimal("10.00"));
        campaignResponse.setCampaignFund(new BigDecimal("500.00"));
        campaignResponse.setIsActive(true);
        campaignResponse.setTown("Warszawa");
        campaignResponse.setRadius(50.0);

        campaignRequest = new CampaignRequest();
        campaignRequest.setName("Test Campaign");
        campaignRequest.setKeywords(List.of("sport", "shoes"));
        campaignRequest.setBidAmount(new BigDecimal("10.00"));
        campaignRequest.setCampaignFund(new BigDecimal("500.00"));
        campaignRequest.setIsActive(true);
        campaignRequest.setTown("Warszawa");
        campaignRequest.setRadius(50.0);
    }

    @Test
    void shouldGetAllCampaigns() throws Exception {
        when(campaignService.getAllCampaigns()).thenReturn(List.of(campaignResponse));

        mockMvc.perform(get("/api/campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Campaign"))
                .andExpect(jsonPath("$[0].town").value("Warszawa"));
    }

    @Test
    void shouldReturnEmptyListWhenNoCampaigns() throws Exception {
        when(campaignService.getAllCampaigns()).thenReturn(List.of());

        mockMvc.perform(get("/api/campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldGetCampaignById() throws Exception {
        when(campaignService.getCampaignById(campaignId)).thenReturn(campaignResponse);

        mockMvc.perform(get("/api/campaigns/{id}", campaignId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Campaign"));
    }

    @Test
    void shouldReturn404WhenCampaignNotFound() throws Exception {
        when(campaignService.getCampaignById(campaignId))
                .thenThrow(new EntityNotFoundException("Campaign not found"));

        mockMvc.perform(get("/api/campaigns/{id}", campaignId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateCampaign() throws Exception {
        when(campaignService.createCampaign(any(CampaignRequest.class)))
                .thenReturn(campaignResponse);

        mockMvc.perform(post("/api/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Campaign"));
    }

    @Test
    void shouldReturn400WhenCreateCampaignWithInvalidData() throws Exception {
        CampaignRequest invalidRequest = new CampaignRequest();
        // puste pola - walidacja powinna zwrócić 400

        mockMvc.perform(post("/api/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenInsufficientFunds() throws Exception {
        when(campaignService.createCampaign(any(CampaignRequest.class)))
                .thenThrow(new RuntimeException("Insufficient funds"));

        mockMvc.perform(post("/api/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateCampaign() throws Exception {
        when(campaignService.updateCampaign(eq(campaignId), any(CampaignRequest.class)))
                .thenReturn(campaignResponse);

        mockMvc.perform(put("/api/campaigns/{id}", campaignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Campaign"));
    }

    @Test
    void shouldReturn404WhenUpdateCampaignNotFound() throws Exception {
        when(campaignService.updateCampaign(eq(campaignId), any(CampaignRequest.class)))
                .thenThrow(new EntityNotFoundException("Campaign not found"));

        mockMvc.perform(put("/api/campaigns/{id}", campaignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteCampaign() throws Exception {
        mockMvc.perform(delete("/api/campaigns/{id}", campaignId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeleteCampaignNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Campaign not found"))
                .when(campaignService).deleteCampaign(campaignId);

        mockMvc.perform(delete("/api/campaigns/{id}", campaignId))
                .andExpect(status().isNotFound());
    }
}