package com.example.backend.service;

import com.example.backend.dto.SellerResponse;
import com.example.backend.entity.Seller;
import com.example.backend.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerResponse getDefaultSeller() {
        Seller seller = sellerRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        return SellerResponse.builder()
                .id(seller.getId())
                .name(seller.getName())
                .emeraldBalance(seller.getEmeraldBalance())
                .build();
    }
}
