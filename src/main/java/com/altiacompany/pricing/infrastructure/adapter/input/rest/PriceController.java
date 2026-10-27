package com.altiacompany.pricing.infrastructure.adapter.input.rest;

import java.time.OffsetDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.altiacompany.pricing.application.port.input.GetPriceQuery;
import com.altiacompany.pricing.application.port.input.GetPriceUseCase;
import com.altiacompany.pricing.domain.model.Price;
import com.altiacompany.pricing.infrastructure.adapter.input.rest.api.PricesApi;
import com.altiacompany.pricing.infrastructure.adapter.input.rest.dto.PriceResponse;

/**
 * REST controller that acts as the entry adapter for pricing-related API
 * endpoints. Implements the generated PricesApi interface.
 */
@RestController
public class PriceController implements PricesApi {

    private final GetPriceUseCase getPriceUseCase;
    private final PriceRestMapper restMapper;

    public PriceController(GetPriceUseCase getPriceUseCase,
            PriceRestMapper restMapper) {
        this.getPriceUseCase = getPriceUseCase;
        this.restMapper = restMapper;
    }

    @Override
    public ResponseEntity<PriceResponse> getApplicablePrice(
            OffsetDateTime applicationDate, Long productId, Long brandId) {
        GetPriceQuery query =
                restMapper.toQuery(applicationDate, productId, brandId);

        Price domainPrice = getPriceUseCase.getApplicablePrice(query);

        PriceResponse response =
                restMapper.toResponse(domainPrice, applicationDate.getOffset());

        return ResponseEntity.ok(response);
    }
}
