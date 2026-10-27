package com.altiacompany.pricing.application.port.output;

import java.time.Instant;
import java.util.List;

import com.altiacompany.pricing.domain.model.Price;

/**
 * Output port defining persistence operations for the {@link Price} domain
 * model.
 */
public interface PriceRepository {

    /**
     * Finds all prices applicable for a product of a specific brand on the
     * given date.
     *
     * @param applicationDate
     *            the date when the price should be active.
     * @param productId
     *            the ID of the product.
     * @param brandId
     *            the ID of the brand.
     * @return a list of all matching {@link Price} models.
     */
    List<Price> findByApplicationDateAndProductIdAndBrandId(
            Instant applicationDate, Long productId, Long brandId);

}
