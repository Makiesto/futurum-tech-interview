package com.example.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SellerResponse {
    private Long id;
    private String name;
    private BigDecimal emeraldBalance;
}