package com.altiacompany.pricing.infrastructure.adapter.input.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

import com.altiacompany.pricing.application.port.input.GetProductsQuery;
import com.altiacompany.pricing.application.port.input.GetProductsUseCase;
import com.altiacompany.pricing.domain.model.Product;
import com.altiacompany.pricing.infrastructure.adapter.input.rest.api.ProductsApi;
import com.altiacompany.pricing.infrastructure.adapter.input.rest.ProductRestMapper;
import com.altiacompany.pricing.infrastructure.adapter.input.rest.dto.ProductResponse;

/**
 * REST controller that acts as the entry adapter for product-related API
 * endpoints. Implements the generated ProductsApi interface.
 */
@RestController
public class ProductController implements ProductsApi {

    private final GetProductsUseCase getProductsUseCase;
    private final ProductRestMapper restMapper;

    public ProductController(GetProductsUseCase getProductsUseCase, ProductRestMapper restMapper) {
        this.getProductsUseCase = getProductsUseCase;
        this.restMapper = restMapper;
    }

    @Override
    public ResponseEntity<List<ProductResponse>> getAllProducts(@Nullable String q) {
        GetProductsQuery query = GetProductsQuery.builder()
                .q(q)
                .build();

        List<Product> domainProducts = getProductsUseCase.getAllProducts(query);
        List<ProductResponse> response = restMapper.toResponse(domainProducts);
        return ResponseEntity.ok(response);
    }
}
