package com.altiacompany.pricing.infrastructure.adapter.output.persistence;

import org.springframework.stereotype.Component;

import com.altiacompany.pricing.domain.model.Price;

/**
 * Mapper component that handles conversion between relational database entities
 * and core domain models.
 */
@Component
public class PricePersistenceMapper {

    /**
     * Converts a PriceEntity database model into a core domain Price model.
     *
     * @param entity
     *            the {@link PriceEntity} containing database records.
     * @return the mapped domain {@link Price} model, or null if the input is
     *         null.
     */
    public Price toDomain(PriceEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Price(entity.getBrandId(), entity.getStartDate(),
                entity.getEndDate(), entity.getPriceList(),
                entity.getProductId(), entity.getPriority(), entity.getAmount(),
                entity.getCurrency());
    }
}