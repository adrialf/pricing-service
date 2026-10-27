package com.altiacompany.pricing.infrastructure.adapter.input.rest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

import com.altiacompany.pricing.application.port.input.GetPriceQuery;
import com.altiacompany.pricing.domain.model.Price;
import com.altiacompany.pricing.infrastructure.adapter.input.rest.dto.PriceResponse;

/**
 * Mapper component that handles conversion between REST request/response DTOs
 * and application/domain query and model objects.
 */
@Component
public class PriceRestMapper {

    /**
     * Converts a domain Price object into a PriceResponse DTO using the
     * provided timezone offset.
     *
     * @param price
     *            the domain {@link Price} model to map.
     * @param offset
     *            the target {@link ZoneOffset} to apply to the timestamps.
     * @return the mapped {@link PriceResponse} DTO, or null if the input is
     *         null.
     */
    public PriceResponse toResponse(Price price, ZoneOffset offset) {
        if (price == null) {
            return null;
        }

        return new PriceResponse(price.getProductId(), price.getBrandId(),
                price.getPriceList(), price.getStartDate().atOffset(offset),
                price.getEndDate().atOffset(offset),
                Double.valueOf(price.getAmount().doubleValue()),
                price.getCurrency());
    }

    /**
     * Converts raw REST query parameters into a structured GetPriceQuery
     * object.
     *
     * @param applicationDate
     *            the date/time of application with timezone offset.
     * @param productId
     *            the unique product identifier.
     * @param brandId
     *            the unique brand identifier.
     * @return the constructed {@link GetPriceQuery} object.
     */
    public GetPriceQuery toQuery(OffsetDateTime applicationDate, Long productId,
            Long brandId) {
        // @formatter:off
        return GetPriceQuery.builder()
                .applicationDate(applicationDate != null ?
                        applicationDate.toInstant() : null)
                .productId(productId)
                .brandId(brandId)
                .build();
        // @formatter:on
    }
}
