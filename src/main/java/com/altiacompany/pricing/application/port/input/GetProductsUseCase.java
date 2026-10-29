package com.altiacompany.pricing.application.port.input;

import java.util.List;

import com.altiacompany.pricing.domain.model.Product;

/**
 * Input port defining the use case for retrieving products.
 */
public interface GetProductsUseCase {

    /**
     * Retrieves all products matching the given criteria.
     *
     * @param query
     *            the query containing the filter criteria.
     * @return a list of {@link Product} models.
     */
    List<Product> getAllProducts(GetProductsQuery query);
}
