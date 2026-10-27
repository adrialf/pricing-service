package com.altiacompany.pricing.application.port.input;

import java.util.List;

import com.altiacompany.pricing.domain.model.Brand;

/**
 * Input port defining the use case for retrieving brands.
 */
public interface GetBrandsUseCase {

    /**
     * Retrieves all brands matching the given criteria.
     *
     * @param query
     *            the query containing the filter criteria.
     * @return a list of {@link Brand} models.
     */
    List<Brand> getAllBrands(GetBrandsQuery query);
}
