package com.altiacompany.pricing.application.service;

import java.util.List;
import java.util.Locale;

import com.altiacompany.pricing.application.port.input.GetProductsQuery;
import com.altiacompany.pricing.application.port.input.GetProductsUseCase;
import com.altiacompany.pricing.application.port.output.ProductRepository;
import com.altiacompany.pricing.domain.model.Product;

/**
 * Application service implementing the {@link GetProductsUseCase}.
 */
public class GetProductsService implements GetProductsUseCase {

    private final ProductRepository productRepository;

    public GetProductsService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts(GetProductsQuery query) {
        return productRepository.findAll().stream()
                .filter(product -> matchesQuery(product, query.getQ()))
                .toList();
    }

    private boolean matchesQuery(Product product, String q) {
        if (q == null || q.isBlank()) {
            return true;
        }

        String normalizedQuery = q.trim().toLowerCase(Locale.ROOT);
        return containsIgnoreCase(product.getName(), normalizedQuery)
                || containsIgnoreCase(product.getDescription(), normalizedQuery);
    }

    private boolean containsIgnoreCase(String value, String normalizedQuery) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedQuery);
    }
}
