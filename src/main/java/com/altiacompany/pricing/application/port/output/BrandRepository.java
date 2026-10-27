package com.altiacompany.pricing.application.port.output;

import java.util.List;
import java.util.Optional;

import com.altiacompany.pricing.domain.model.Brand;

/**
 * Output port defining persistence operations for the {@link Brand} domain
 * model.
 */
public interface BrandRepository {

    /**
     * Finds all brands.
     *
     * @return a list of all {@link Brand} models.
     */
    List<Brand> findAll();

    /**
     * Finds a brand by its ID.
     *
     * @param id
     *            the ID of the brand.
     * @return an {@link Optional} containing the {@link Brand} if found, or
     *         empty otherwise.
     */
    Optional<Brand> findById(Long id);

}
