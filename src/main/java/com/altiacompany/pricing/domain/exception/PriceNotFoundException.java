package com.altiacompany.pricing.domain.exception;

import java.time.Instant;

/**
 * Domain exception thrown when no pricing configurations match the query
 * criteria.
 */
public class PriceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PriceNotFoundException(Instant applicationDate, Long productId,
            Long brandId) {
        super(String.format(
                "No applicable price found for productId %d, brandId %d on %s",
                productId, brandId, applicationDate));
    }

}
