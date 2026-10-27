package com.altiacompany.pricing.infrastructure.adapter.output.persistence;

import org.springframework.stereotype.Component;

import com.altiacompany.pricing.domain.model.Brand;

/**
 * Mapper for Brand domain model and BrandEntity.
 */
@Component
public class BrandPersistenceMapper {

    public Brand toDomain(BrandEntity entity) {
        return new Brand(entity.getId(), entity.getName(), entity.getCode(),
                entity.getDescription(), entity.getActive());
    }

    public BrandEntity toEntity(Brand domain) {
        BrandEntity entity = new BrandEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setCode(domain.getCode());
        entity.setDescription(domain.getDescription());
        entity.setActive(domain.getActive());
        return entity;
    }
}
