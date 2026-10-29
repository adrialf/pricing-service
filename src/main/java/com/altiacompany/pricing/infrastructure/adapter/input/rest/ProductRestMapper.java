package com.altiacompany.pricing.infrastructure.adapter.input.rest;

import java.util.List;

import org.springframework.stereotype.Component;

import com.altiacompany.pricing.domain.model.Product;
import com.altiacompany.pricing.infrastructure.adapter.input.rest.dto.ProductResponse;

/**
 * Mapper for Product domain model and ProductResponse DTO.
 */
@Component
public class ProductRestMapper {

    public ProductResponse toResponse(Product domain) {
        ProductResponse response = new ProductResponse();
        response.setId(domain.getId());
        response.setName(domain.getName());
        response.setDescription(domain.getDescription());
        return response;
    }

    public List<ProductResponse> toResponse(List<Product> domainList) {
        return domainList.stream().map(this::toResponse).toList();
    }
}
