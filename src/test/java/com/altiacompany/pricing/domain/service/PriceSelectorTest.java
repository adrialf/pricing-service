package com.altiacompany.pricing.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.altiacompany.pricing.domain.model.Price;

@Tag("unit")
class PriceSelectorTest {

    private final PriceSelector priceSelector = new PriceSelector();

    @Test
    void shouldReturnPriceWhenSinglePriceActive() {
        // Given
        Instant date = Instant.parse("2020-06-14T10:00:00Z");
        Long productId = 35455L;
        Long brandId = 1L;

        Price singlePrice = new Price(brandId, date, date.plusSeconds(3600), 1L,
                productId, 0, BigDecimal.valueOf(35.50), "EUR");

        List<Price> prices = Collections.singletonList(singlePrice);

        // When
        Optional<Price> result = priceSelector.selectApplicablePrice(prices);

        // Then
        assertThat(result).hasValue(singlePrice);
    }

    @Test
    void shouldReturnPriceWithHighestPriorityWhenMultiplePricesActive() {
        // Given
        Instant date = Instant.parse("2020-06-14T10:00:00Z");
        Long productId = 35455L;
        Long brandId = 1L;

        Price lowPriorityPrice =
                new Price(brandId, date, date.plusSeconds(3600), 1L, productId,
                        0, BigDecimal.valueOf(35.50), "EUR");
        Price highPriorityPrice =
                new Price(brandId, date, date.plusSeconds(3600), 2L, productId,
                        1, BigDecimal.valueOf(25.45), "EUR");

        List<Price> prices = Arrays.asList(lowPriorityPrice, highPriorityPrice);

        // When
        Optional<Price> result = priceSelector.selectApplicablePrice(prices);

        // Then
        assertThat(result).hasValue(highPriorityPrice);
    }

    @Test
    void shouldReturnEmptyOptionalWhenListIsEmpty() {
        // Given
        List<Price> prices = Collections.emptyList();

        // When
        Optional<Price> result = priceSelector.selectApplicablePrice(prices);

        // Then
        assertThat(result).isEmpty();
    }
}
