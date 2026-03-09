package com.example.backend;

import com.example.backend.entity.KeywordSuggestion;
import com.example.backend.entity.Seller;
import com.example.backend.repository.KeywordSuggestionRepository;
import com.example.backend.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final List<String> KEYWORDS = List.of(
            "sport", "shoes", "electronics", "clothing",
            "fitness", "home appliances", "TV", "furniture",
            "toys", "books", "gaming", "outdoor"
    );

    private final SellerRepository sellerRepository;
    private final KeywordSuggestionRepository keywordRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (sellerRepository.count() == 0) {
            Seller seller = new Seller();
            seller.setName("Test Seller");
            seller.setEmeraldBalance(new BigDecimal("10000.00"));
            sellerRepository.save(seller);
        }


        if (keywordRepository.count() == 0) {
            KEYWORDS.forEach(value -> {
                KeywordSuggestion keyword = new KeywordSuggestion();
                keyword.setValue(value);
                keywordRepository.save(keyword);
            });
        }
    }
}