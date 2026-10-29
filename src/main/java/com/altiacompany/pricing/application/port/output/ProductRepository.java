package com.altiacompany.pricing.application.port.output;

import java.util.List;

import com.altiacompany.pricing.domain.model.Product;

/**
 * Output port defining persistence operations for the {@link Product} domain
 * model.
 */
public interface ProductRepository {

    /**
     * Finds all products.
     *
     * @return a list of all {@link Product} models.
     */
    List<Product> findAll();

}
