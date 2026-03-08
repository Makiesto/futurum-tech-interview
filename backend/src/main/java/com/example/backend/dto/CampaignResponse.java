package com.example.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CampaignResponse {
    private UUID id;
    private String name;
    private List<String> keywords;
    private BigDecimal bidAmount;
    private BigDecimal campaignFund;
    private Boolean isActive;
    private String town;
    private Double radius;
}