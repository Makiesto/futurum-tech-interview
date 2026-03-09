package com.example.backend.service;

import com.example.backend.dto.SellerResponse;
import com.example.backend.entity.Seller;
import com.example.backend.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerService sellerService;

    private Seller seller;

    @BeforeEach
    void setUp() {
        seller = new Seller();
        seller.setId(1L);
        seller.setName("Test Seller");
        seller.setEmeraldBalance(new BigDecimal("10000.00"));
    }

    @Test
    void shouldReturnDefaultSeller() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        SellerResponse result = sellerService.getDefaultSeller();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Seller");
        assertThat(result.getEmeraldBalance()).isEqualByComparingTo("10000.00");
    }

    @Test
    void shouldThrowExceptionWhenSellerNotFound() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> sellerService.getDefaultSeller());
    }
}