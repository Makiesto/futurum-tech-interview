package com.example.backend.controller;

import com.example.backend.dto.CampaignRequest;
import com.example.backend.dto.CampaignResponse;
import com.example.backend.service.CampaignService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
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

    private CampaignResponse buildCampaignResponse(UUID id) {
        return CampaignResponse.builder()
                .id(id)
                .name("Test Campaign")
                .keywords(List.of("sport", "shoes"))
                .bidAmount(new BigDecimal("10.00"))
                .campaignFund(new BigDecimal("500.00"))
                .isActive(true)
                .town("Warszawa")
                .radius(50.0)
                .build();
    }

    private CampaignRequest buildCampaignRequest() {
        CampaignRequest request = new CampaignRequest();
        request.setName("Test Campaign");
        request.setKeywords(List.of("sport", "shoes"));
        request.setBidAmount(new BigDecimal("10.00"));
        request.setCampaignFund(new BigDecimal("500.00"));
        request.setIsActive(true);
        request.setTown("Warszawa");
        request.setRadius(50.0);
        return request;
    }

    @Test
    void shouldGetAllCampaigns() throws Exception {
        UUID id = UUID.randomUUID();
        when(campaignService.getAllCampaigns()).thenReturn(List.of(buildCampaignResponse(id)));

        mockMvc.perform(get("/api/campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Campaign"));
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
        UUID id = UUID.randomUUID();
        when(campaignService.getCampaignById(id)).thenReturn(buildCampaignResponse(id));

        mockMvc.perform(get("/api/campaigns/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Campaign"));
    }

    @Test
    void shouldReturn404WhenCampaignNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(campaignService.getCampaignById(id))
                .thenThrow(new EntityNotFoundException("Campaign not found"));

        mockMvc.perform(get("/api/campaigns/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateCampaign() throws Exception {
        UUID id = UUID.randomUUID();
        when(campaignService.createCampaign(any(CampaignRequest.class)))
                .thenReturn(buildCampaignResponse(id));

        mockMvc.perform(post("/api/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCampaignRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Campaign"));
    }

    @Test
    void shouldReturn400WhenCreateCampaignWithInvalidData() throws Exception {
        CampaignRequest invalidRequest = new CampaignRequest();

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
                        .content(objectMapper.writeValueAsString(buildCampaignRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateCampaign() throws Exception {
        UUID id = UUID.randomUUID();
        when(campaignService.updateCampaign(eq(id), any(CampaignRequest.class)))
                .thenReturn(buildCampaignResponse(id));

        mockMvc.perform(put("/api/campaigns/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCampaignRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Campaign"));
    }

    @Test
    void shouldReturn404WhenUpdateCampaignNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(campaignService.updateCampaign(eq(id), any(CampaignRequest.class)))
                .thenThrow(new EntityNotFoundException("Campaign not found"));

        mockMvc.perform(put("/api/campaigns/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCampaignRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteCampaign() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/campaigns/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeleteCampaignNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Campaign not found"))
                .when(campaignService).deleteCampaign(id);

        mockMvc.perform(delete("/api/campaigns/{id}", id))
                .andExpect(status().isNotFound());
    }
}