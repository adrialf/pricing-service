package com.altiacompany.pricing.infrastructure.adapter.input.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

import com.altiacompany.pricing.application.port.input.GetBrandsQuery;
import com.altiacompany.pricing.application.port.input.GetBrandsUseCase;
import com.altiacompany.pricing.domain.model.Brand;
import com.altiacompany.pricing.infrastructure.adapter.input.rest.api.BrandsApi;
import com.altiacompany.pricing.infrastructure.adapter.input.rest.BrandRestMapper;
import com.altiacompany.pricing.infrastructure.adapter.input.rest.dto.BrandResponse;

/**
 * REST controller that acts as the entry adapter for brand-related API
 * endpoints. Implements the generated BrandsApi interface.
 */
@RestController
public class BrandController implements BrandsApi {

    private final GetBrandsUseCase getBrandsUseCase;
    private final BrandRestMapper restMapper;

    public BrandController(GetBrandsUseCase getBrandsUseCase, BrandRestMapper restMapper) {
        this.getBrandsUseCase = getBrandsUseCase;
        this.restMapper = restMapper;
    }

    @Override
    public ResponseEntity<List<BrandResponse>> getAllBrands(
            @Nullable Long id, @Nullable Boolean active, @Nullable String q) {
        GetBrandsQuery query = GetBrandsQuery.builder()
                .id(id)
                .active(active)
                .q(q)
                .build();

        List<Brand> domainBrands = getBrandsUseCase.getAllBrands(query);
        List<BrandResponse> response = restMapper.toResponse(domainBrands);
        return ResponseEntity.ok(response);
    }
}
