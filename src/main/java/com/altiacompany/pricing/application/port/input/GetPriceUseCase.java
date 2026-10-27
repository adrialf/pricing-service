package com.altiacompany.pricing.application.port.input;

import com.altiacompany.pricing.domain.exception.PriceNotFoundException;
import com.altiacompany.pricing.domain.model.Price;

/**
 * Input port defining the use case for retrieving the applicable price for a
 * product.
 */
public interface GetPriceUseCase {

    /**
     * Retrieves the applicable price for a product of a specific brand at a
     * given date.
     *
     * If multiple price entries match the criteria, the one with the highest
     * priority is returned.
     *
     * @param query
     *            the query containing the brand ID, product ID, and the
     *            application date.
     * @return the applicable {@link Price} domain model.
     * @throws PriceNotFoundException
     *             if no price is applicable.
     */
    Price getApplicablePrice(GetPriceQuery query);

}
