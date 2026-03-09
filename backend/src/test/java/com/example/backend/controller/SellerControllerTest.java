package com.example.backend.controller;

import com.example.backend.dto.SellerResponse;
import com.example.backend.service.SellerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerControllerTest {

    @Mock
    private SellerService sellerService;

    @InjectMocks
    private SellerController sellerController;

    @Test
    void shouldReturnSeller() {
        SellerResponse sellerResponse = new SellerResponse();
        sellerResponse.setId(1L);
        sellerResponse.setName("Test Seller");
        sellerResponse.setEmeraldBalance(new BigDecimal("10000.00"));

        when(sellerService.getDefaultSeller()).thenReturn(sellerResponse);

        ResponseEntity<SellerResponse> result = sellerController.getSeller();

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getName()).isEqualTo("Test Seller");
        assertThat(result.getBody().getEmeraldBalance()).isEqualByComparingTo("10000.00");
    }
}