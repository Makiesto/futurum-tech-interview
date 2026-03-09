package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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