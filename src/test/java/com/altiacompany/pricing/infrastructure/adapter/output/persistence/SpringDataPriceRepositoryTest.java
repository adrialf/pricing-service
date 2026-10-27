package com.altiacompany.pricing.infrastructure.adapter.output.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
                createPriceEntity(1L, Instant.parse("2020-06-14T00:00:00Z"),
                        Instant.parse("2020-12-31T23:59:59Z"), 1L, 35455L, 0,
                        BigDecimal.valueOf(35.50), "EUR"));
        // 2. PriceList 2: Priority 1, Price 25.45
        price2 = repository.save(
                createPriceEntity(1L, Instant.parse("2020-06-14T15:00:00Z"),
                        Instant.parse("2020-06-14T18:30:00Z"), 2L, 35455L, 1,
                        BigDecimal.valueOf(25.45), "EUR"));
        // 3. PriceList 3: Priority 1, Price 30.50
        repository.save(createPriceEntity(1L,
                Instant.parse("2020-06-15T00:00:00Z"),
                Instant.parse("2020-06-15T11:00:00Z"), 3L, 35455L, 1,
                BigDecimal.valueOf(30.50), "EUR"));
        // 4. PriceList 4: Priority 1, Price 38.95
        repository.save(createPriceEntity(1L,
                Instant.parse("2020-06-15T16:00:00Z"),
                Instant.parse("2020-12-31T23:59:59Z"), 4L, 35455L, 1,
                BigDecimal.valueOf(38.95), "EUR"));
    }

    @Test
    void shouldFindPricesByApplicationDateProductIdAndBrandId() {
        // Given
        Instant date = Instant.parse("2020-06-14T10:00:00Z");
        Long productId = 35455L;
        Long brandId = 1L;

        // When
        List<PriceEntity> prices =
                repository.findByApplicationDateAndProductIdAndBrandId(date,
                        productId, brandId);

        // Then
        assertThat(prices).hasSize(1).containsExactly(price1);
    }

    @Test
    void shouldFindMultiplePricesWhenDateOverlapsMultipleIntervals() {
        // Given - Date when two prices are active (price1 and price2)
        Instant date = Instant.parse("2020-06-14T16:00:00Z");
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
    void shouldFindPriceAtEndDateBoundary() {
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

    private PriceEntity createPriceEntity(Long brandId, Instant startDate,
            Instant endDate, Long priceList, Long productId, Integer priority,
            BigDecimal price, String curr) {
        PriceEntity priceEntity = new PriceEntity();
        priceEntity.setBrandId(brandId);
        priceEntity.setStartDate(startDate);
        priceEntity.setEndDate(endDate);
        priceEntity.setPriceList(priceList);
        priceEntity.setProductId(productId);
        priceEntity.setPriority(priority);
        priceEntity.setAmount(price);
        priceEntity.setCurrency(curr);
        return priceEntity;
    }
}
