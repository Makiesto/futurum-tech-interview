package com.example.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CampaignResponse {
    private Long id;
    private String name;
    private List<String> keywords;
    private BigDecimal bidAmount;
    private BigDecimal campaignFund;
    private Boolean isActive;
    private String town;
    private Double radius;
}