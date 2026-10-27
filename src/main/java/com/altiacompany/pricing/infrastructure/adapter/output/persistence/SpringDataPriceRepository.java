package com.altiacompany.pricing.infrastructure.adapter.output.persistence;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SpringDataPriceRepository
        extends CrudRepository<PriceEntity, Long> {

    @Query("SELECT p FROM PriceEntity p WHERE p.productId = :productId "
            + "AND p.brandId = :brandId "
            + "AND :applicationDate BETWEEN p.startDate AND p.endDate")
    List<PriceEntity> findByApplicationDateAndProductIdAndBrandId(
            @Param("applicationDate") Instant applicationDate,
            @Param("productId") Long productId, @Param("brandId") Long brandId);
}
