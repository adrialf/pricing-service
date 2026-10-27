package com.altiacompany.pricing.domain.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import com.altiacompany.pricing.domain.model.Price;

/**
 * Domain service containing rules for selecting the appropriate price from a
 * list.
 */
public class PriceSelector {

    /**
     * Selects the applicable price with the highest priority from a list of
     * active prices.
     *
     * @param prices
     *            the list of candidate prices active at the given date.
     * @return an {@link Optional} containing the selected {@link Price} with
     *         the highest priority. If multiple prices share the same maximum
     *         priority, the first one encountered in the collection is
     *         returned. Returns {@link Optional#empty()} if the input
     *         collection is empty.
     */
    public Optional<Price> selectApplicablePrice(Collection<Price> prices) {
        return prices.stream().max(Comparator.comparingInt(Price::getPriority));
    }
}
