package com.altiacompany.pricing.infrastructure.adapter.output.persistence;

import org.springframework.stereotype.Component;

import com.altiacompany.pricing.domain.model.Product;

/**
 * Mapper for Product domain model and ProductEntity.
 */
@Component
public class ProductPersistenceMapper {

    public Product toDomain(ProductEntity entity) {
        return new Product(entity.getId(), entity.getName(), entity.getDescription());
    }

    public ProductEntity toEntity(Product domain) {
        ProductEntity entity = new ProductEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        return entity;
    }
}
