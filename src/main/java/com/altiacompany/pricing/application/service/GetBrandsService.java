package com.altiacompany.pricing.application.service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.altiacompany.pricing.application.port.input.GetBrandsQuery;
import com.altiacompany.pricing.application.port.input.GetBrandsUseCase;
import com.altiacompany.pricing.application.port.output.BrandRepository;
import com.altiacompany.pricing.domain.model.Brand;

/**
 * Application service implementing the {@link GetBrandsUseCase}.
 */
public class GetBrandsService implements GetBrandsUseCase {

    private final BrandRepository brandRepository;

    public GetBrandsService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public List<Brand> getAllBrands(GetBrandsQuery query) {
        return brandRepository.findAll().stream()
                .filter(brand -> query.getId() == null
                        || Objects.equals(brand.getId(), query.getId()))
                .filter(brand -> query.getActive() == null
                        || Objects.equals(brand.getActive(), query.getActive()))
                .filter(brand -> matchesQuery(brand, query.getQ()))
                .toList();
    }

    private boolean matchesQuery(Brand brand, String q) {
        if (q == null || q.isBlank()) {
            return true;
        }

        String normalizedQuery = q.trim().toLowerCase(Locale.ROOT);
        return containsIgnoreCase(brand.getName(), normalizedQuery)
                || containsIgnoreCase(brand.getCode(), normalizedQuery)
                || containsIgnoreCase(brand.getDescription(), normalizedQuery);
    }

    private boolean containsIgnoreCase(String value, String normalizedQuery) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedQuery);
    }
}
