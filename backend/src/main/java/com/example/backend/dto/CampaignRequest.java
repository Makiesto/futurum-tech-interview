package com.example.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CampaignRequest {

    @NotBlank(message = "Campaign name is mandatory")
    private String name;

    @NotEmpty(message = "At least one keyword is required")
    private List<String> keywords;

    @NotNull(message = "Bid amount is mandatory")
    @DecimalMin(value = "0.01", message = "Bid amount must be at least 0.01")
    private BigDecimal bidAmount;

    @NotNull(message = "Campaign fund is mandatory")
    @DecimalMin(value = "0.01", message = "Campaign fund must be at least 0.01")
    private BigDecimal campaignFund;

    @NotNull(message = "Status is mandatory")
    private Boolean isActive;

    private String town;

    @NotNull(message = "Radius is mandatory")
    @Min(value = 1, message = "Radius must be at least 1km")
    private Double radius;
}
