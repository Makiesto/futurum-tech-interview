package com.example.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @NotBlank(message = "Campaign name is mandatory")
    private String name;

    @ElementCollection
    @Size(min=1, message = "At least one keyword is required")
    private List<String> keywords;

    @NotNull(message = "Bid amount is mandatory")
    @DecimalMin(value = "0.01", message = "Bid amount must be at least 0.01")
    private BigDecimal bidAmount;

    @NotNull(message = "Campaign fund is mandatory")
    @DecimalMin(value = "0.01", message = "Campaign fund must be at least 0.01")
    private BigDecimal campaignFund;

    @NotNull
    private Boolean status;

    private String town;

    @NotNull(message = "Radius is mandatory")
    @Min(value = 1, message = "Radius must be at least 1km")
    private Double radius;


}
