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

        SellerResponse response = new SellerResponse();
        response.setId(seller.getId());
        response.setName(seller.getName());
        response.setEmeraldBalance(seller.getEmeraldBalance());
        return response;
    }
}
