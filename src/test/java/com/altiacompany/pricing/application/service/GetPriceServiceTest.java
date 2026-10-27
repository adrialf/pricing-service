package com.altiacompany.pricing.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.altiacompany.pricing.application.port.input.GetPriceQuery;
import com.altiacompany.pricing.application.port.output.PriceRepository;
import com.altiacompany.pricing.domain.exception.PriceNotFoundException;
import com.altiacompany.pricing.domain.model.Price;
import com.altiacompany.pricing.domain.service.PriceSelector;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class GetPriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private PriceSelector priceSelector;

    @InjectMocks
    private GetPriceService getPriceService;

    @Test
    void shouldDelegateToRepositoryAndSelectorToReturnPrice() {
        // Given
        Instant date = Instant.parse("2020-06-14T10:00:00Z");
        Long productId = 35455L;
        Long brandId = 1L;

        // @formatter:off
        GetPriceQuery query = GetPriceQuery.builder()
                .applicationDate(date)
                .productId(productId)
                .brandId(brandId)
                .build();
        // @formatter:on

        Price expectedPrice = new Price(brandId, date, date.plusSeconds(86400),
                1L, productId, 0, new BigDecimal("25.50"), "EUR");
        List<Price> prices = List.of(expectedPrice);

        when(priceRepository.findByApplicationDateAndProductIdAndBrandId(date,
                productId, brandId)).thenReturn(prices);
        when(priceSelector.selectApplicablePrice(prices)).thenReturn(
                Optional.of(expectedPrice));

        // When
        Price result = getPriceService.getApplicablePrice(query);

        // Then
        assertThat(result).isSameAs(expectedPrice);
    }

    @Test
    void shouldThrowPriceNotFoundExceptionWhenSelectorReturnsEmptyOptional() {
        // Given
        Instant date = Instant.parse("2020-06-14T10:00:00Z");
        Long productId = 35455L;
        Long brandId = 1L;

        // @formatter:off
        GetPriceQuery query = GetPriceQuery.builder()
                .applicationDate(date)
                .productId(productId)
                .brandId(brandId)
                .build();
        // @formatter:on

        List<Price> emptyList = Collections.emptyList();
        when(priceRepository.findByApplicationDateAndProductIdAndBrandId(date,
                productId, brandId)).thenReturn(emptyList);
        when(priceSelector.selectApplicablePrice(emptyList)).thenReturn(
                Optional.empty());

        // When & Then
        // @formatter:off
        assertThatThrownBy(() -> getPriceService.getApplicablePrice(query))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining("No applicable price found")
                .hasMessageContaining("productId 35455")
                .hasMessageContaining("brandId 1")
                .hasMessageContaining("2020-06-14T10:00:00Z");
        // @formatter:on
    }
}
