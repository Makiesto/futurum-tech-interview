package com.example.backend.service;

import com.example.backend.dto.CampaignRequest;
import com.example.backend.dto.CampaignResponse;
import com.example.backend.entity.Campaign;
import com.example.backend.entity.Seller;
import com.example.backend.repository.CampaignRepository;
import com.example.backend.repository.SellerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private CampaignService campaignService;

    private Seller seller;
    private Campaign campaign;
    private CampaignRequest request;
    private UUID campaignId;

    @BeforeEach
    void setUp() {
        campaignId = UUID.randomUUID();

        seller = new Seller();
        seller.setId(1L);
        seller.setName("Test Seller");
        seller.setEmeraldBalance(new BigDecimal("10000.00"));

        campaign = new Campaign();
        campaign.setId(campaignId);
        campaign.setName("Test Campaign");
        campaign.setKeywords(List.of("sport", "shoes"));
        campaign.setBidAmount(new BigDecimal("10.00"));
        campaign.setCampaignFund(new BigDecimal("500.00"));
        campaign.setIsActive(true);
        campaign.setTown("Warszawa");
        campaign.setRadius(50.0);
        campaign.setSeller(seller);

        request = new CampaignRequest();
        request.setName("Test Campaign");
        request.setKeywords(List.of("sport", "shoes"));
        request.setBidAmount(new BigDecimal("10.00"));
        request.setCampaignFund(new BigDecimal("500.00"));
        request.setIsActive(true);
        request.setTown("Warszawa");
        request.setRadius(50.0);
    }

    // ============ GET ALL ============

    @Test
    void shouldReturnAllCampaigns() {
        when(campaignRepository.findAll()).thenReturn(List.of(campaign));

        List<CampaignResponse> result = campaignService.getAllCampaigns();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Test Campaign");
    }

    @Test
    void shouldReturnEmptyListWhenNoCampaigns() {
        when(campaignRepository.findAll()).thenReturn(List.of());

        List<CampaignResponse> result = campaignService.getAllCampaigns();

        assertThat(result).isEmpty();
    }

    // ============ GET BY ID ============

    @Test
    void shouldReturnCampaignById() {
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));

        CampaignResponse result = campaignService.getCampaignById(campaignId);

        assertThat(result.getId()).isEqualTo(campaignId);
        assertThat(result.getName()).isEqualTo("Test Campaign");
    }

    @Test
    void shouldThrowExceptionWhenCampaignNotFound() {
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> campaignService.getCampaignById(campaignId));
    }

    // ============ CREATE ============

    @Test
    void shouldCreateCampaignAndDeductBalance() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);

        CampaignResponse result = campaignService.createCampaign(request);

        assertThat(result.getName()).isEqualTo("Test Campaign");
        assertThat(seller.getEmeraldBalance()).isEqualByComparingTo("9500.00");
        verify(sellerRepository).save(seller);
        verify(campaignRepository).save(any(Campaign.class));
    }

    @Test
    void shouldThrowExceptionWhenInsufficientFundsOnCreate() {
        seller.setEmeraldBalance(new BigDecimal("100.00"));
        request.setCampaignFund(new BigDecimal("500.00"));

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        assertThrows(RuntimeException.class,
                () -> campaignService.createCampaign(request));

        verify(campaignRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSellerNotFoundOnCreate() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> campaignService.createCampaign(request));
    }

    // ============ UPDATE ============

    @Test
    void shouldUpdateCampaignAndAdjustBalance() {
        request.setCampaignFund(new BigDecimal("600.00"));

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);

        campaignService.updateCampaign(campaignId, request);

        // 10000 + 500 (stary fund) - 600 (nowy fund) = 9900
        assertThat(seller.getEmeraldBalance()).isEqualByComparingTo("9900.00");
    }

    @Test
    void shouldThrowExceptionWhenInsufficientFundsOnUpdate() {
        seller.setEmeraldBalance(new BigDecimal("100.00"));
        request.setCampaignFund(new BigDecimal("5000.00"));

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        assertThrows(RuntimeException.class,
                () -> campaignService.updateCampaign(campaignId, request));

        verify(campaignRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCampaignNotFoundOnUpdate() {
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> campaignService.updateCampaign(campaignId, request));
    }

    // ============ DELETE ============

    @Test
    void shouldDeleteCampaignAndReturnFundToBalance() {
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        campaignService.deleteCampaign(campaignId);

        // 10000 + 500 (zwrot fundu) = 10500
        assertThat(seller.getEmeraldBalance()).isEqualByComparingTo("10500.00");
        verify(campaignRepository).delete(campaign);
    }

    @Test
    void shouldThrowExceptionWhenCampaignNotFoundOnDelete() {
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> campaignService.deleteCampaign(campaignId));

        verify(campaignRepository, never()).delete(any());
    }
}