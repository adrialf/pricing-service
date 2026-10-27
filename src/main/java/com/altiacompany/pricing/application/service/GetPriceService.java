package com.altiacompany.pricing.application.service;

import java.util.List;

import com.altiacompany.pricing.application.port.input.GetPriceQuery;
import com.altiacompany.pricing.application.port.input.GetPriceUseCase;
import com.altiacompany.pricing.application.port.output.PriceRepository;
import com.altiacompany.pricing.domain.exception.PriceNotFoundException;
import com.altiacompany.pricing.domain.model.Price;
import com.altiacompany.pricing.domain.service.PriceSelector;

/**
 * Application service that orchestrates the use case for retrieving the
 * applicable product price.
 */
public class GetPriceService implements GetPriceUseCase {

    private final PriceRepository priceRepository;
    private final PriceSelector priceSelector;

    public GetPriceService(PriceRepository priceRepository,
            PriceSelector priceSelector) {
        this.priceRepository = priceRepository;
        this.priceSelector = priceSelector;
    }

    @Override
    public Price getApplicablePrice(GetPriceQuery query) {
        /*
         * The final selection of the applicable price is intentionally
         * performed by the domain layer to keep the business rule independent
         * from the persistence layer. This helps ensure that future changes to
         * the price selection rules remain isolated within the domain layer.
         *
         * However, this design assumes a very small number of overlapping
         * prices. If that assumption no longer holds, the persistence adapter
         * may optimize the query (e.g. ORDER BY priority DESC LIMIT 1) while
         * preserving the same repository contract and observable business
         * behaviour.
         */
        List<Price> prices =
                priceRepository.findByApplicationDateAndProductIdAndBrandId(
                        query.getApplicationDate(), query.getProductId(),
                        query.getBrandId());

        return priceSelector.selectApplicablePrice(prices).orElseThrow(
                () -> new PriceNotFoundException(query.getApplicationDate(),
                        query.getProductId(), query.getBrandId()));
    }
}
