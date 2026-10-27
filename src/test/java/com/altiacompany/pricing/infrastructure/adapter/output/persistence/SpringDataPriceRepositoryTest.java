package com.altiacompany.pricing.infrastructure.adapter.output.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@Tag("repository")
@DataJpaTest
class SpringDataPriceRepositoryTest {

    @Autowired
    private SpringDataPriceRepository repository;

    private PriceEntity price1;
    private PriceEntity price2;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        // 1. PriceList 1: Priority 0, Price 35.50
        price1 = repository.save(
                new PriceEntity(null, 1L, Instant.parse("2020-06-14T00:00:00Z"),
                        Instant.parse("2020-12-31T23:59:59Z"), 1L, 35455L, 0,
                        BigDecimal.valueOf(35.50), "EUR"));

        // 2. PriceList 2: Priority 1, Price 25.45
        price2 = repository.save(
                new PriceEntity(null, 1L, Instant.parse("2020-06-14T15:00:00Z"),
                        Instant.parse("2020-06-14T18:30:00Z"), 2L, 35455L, 1,
                        BigDecimal.valueOf(25.45), "EUR"));
    }

    @Test
    void shouldFindPricesActiveAtSpecificDate() {
        // Given - Date when a single price is active
        Instant date = Instant.parse("2020-06-14T19:00:00Z");
        Long productId = 35455L;
        Long brandId = 1L;

        // When
        List<PriceEntity> prices =
                repository.findByApplicationDateAndProductIdAndBrandId(date,
                        productId, brandId);

        // Then
        assertThat(prices).hasSize(1).containsExactlyInAnyOrder(price1);
    }

    @Test
    void shouldFindPricesExactlyOnStartBoundary() {
        // Given - Date exactly at the start boundary
        Instant date = Instant.parse("2020-06-14T15:00:00Z");
        Long productId = 35455L;
        Long brandId = 1L;

        // When
        List<PriceEntity> prices =
                repository.findByApplicationDateAndProductIdAndBrandId(date,
                        productId, brandId);

        // Then
        assertThat(prices).hasSize(2).containsExactlyInAnyOrder(price1, price2);
    }

    @Test
    void shouldFindPricesExactlyOnEndBoundary() {
        // Given - Date exactly at the end boundary
        Instant date = Instant.parse("2020-06-14T18:30:00Z");
        Long productId = 35455L;
        Long brandId = 1L;

        // When
        List<PriceEntity> prices =
                repository.findByApplicationDateAndProductIdAndBrandId(date,
                        productId, brandId);

        // Then
        assertThat(prices).hasSize(2).containsExactlyInAnyOrder(price1, price2);
    }

    @Test
    void shouldReturnEmptyListWhenNoPricesActiveAtDate() {
        // Given - Date when no price is active
        Instant date = Instant.parse("2019-01-01T00:00:00Z");
        Long productId = 35455L;
        Long brandId = 1L;

        // When
        List<PriceEntity> prices =
                repository.findByApplicationDateAndProductIdAndBrandId(date,
                        productId, brandId);

        // Then
        assertThat(prices).isEmpty();
    }
}