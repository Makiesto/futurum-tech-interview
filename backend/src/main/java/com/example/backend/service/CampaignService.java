package com.example.backend.service;

import com.example.backend.dto.CampaignRequest;
import com.example.backend.dto.CampaignResponse;
import com.example.backend.entity.Campaign;
import com.example.backend.entity.Seller;
import com.example.backend.repository.CampaignRepository;
import com.example.backend.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final SellerRepository sellerRepository;

    public List<CampaignResponse> getAllCampaigns() {
        return campaignRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CampaignResponse getCampaignById(UUID id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        return toResponse(campaign);
    }

    @Transactional
    public CampaignResponse createCampaign(CampaignRequest request) {
        Seller seller = getDefaultSeller();

        // check if seller has enough money
        if (seller.getEmeraldBalance().compareTo(request.getCampaignFund()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        // subtract fund from balance
        seller.setEmeraldBalance(seller.getEmeraldBalance().subtract(request.getCampaignFund()));
        sellerRepository.save(seller);

        // save campaign
        Campaign campaign = toEntity(request, seller);
        return toResponse(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignResponse updateCampaign(UUID id, CampaignRequest request) {
        Campaign existing = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));
        Seller seller = getDefaultSeller();

        // return old fund to balance
        seller.setEmeraldBalance(seller.getEmeraldBalance().add(existing.getCampaignFund()));

        // check if seller has enough balance for new fund
        if (seller.getEmeraldBalance().compareTo(request.getCampaignFund()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        seller.setEmeraldBalance(seller.getEmeraldBalance().subtract(request.getCampaignFund()));
        sellerRepository.save(seller);

        // campaign update
        updateCampaignFields(existing, request);

        return toResponse(campaignRepository.save(existing));
    }

    @Transactional
    public void deleteCampaign(UUID id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        Seller seller = getDefaultSeller();

        // return fund to balance on deletion
        seller.setEmeraldBalance(seller.getEmeraldBalance().add(campaign.getCampaignFund()));
        sellerRepository.save(seller);

        campaignRepository.delete(campaign);
    }

    private Seller getDefaultSeller() {
        return sellerRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
    }

    private CampaignResponse toResponse(Campaign campaign) {
        CampaignResponse response = new CampaignResponse();
        response.setId(campaign.getId());
        response.setName(campaign.getName());
        response.setKeywords(campaign.getKeywords());
        response.setBidAmount(campaign.getBidAmount());
        response.setCampaignFund(campaign.getCampaignFund());
        response.setIsActive(campaign.getIsActive());
        response.setTown(campaign.getTown());
        response.setRadius(campaign.getRadius());
        return response;
    }

    private Campaign toEntity(CampaignRequest request, Seller seller) {
        Campaign campaign = new Campaign();
        updateCampaignFields(campaign, request);
        campaign.setSeller(seller);
        return campaign;
    }

    private void updateCampaignFields(Campaign campaign, CampaignRequest request) {
        campaign.setName(request.getName());
        campaign.setKeywords(request.getKeywords());
        campaign.setBidAmount(request.getBidAmount());
        campaign.setCampaignFund(request.getCampaignFund());
        campaign.setIsActive(request.getIsActive());
        campaign.setTown(request.getTown());
        campaign.setRadius(request.getRadius());
    }


}
