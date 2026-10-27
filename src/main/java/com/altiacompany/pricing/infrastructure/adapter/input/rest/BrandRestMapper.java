package com.altiacompany.pricing.infrastructure.adapter.input.rest;

import java.util.List;

import org.springframework.stereotype.Component;

import com.altiacompany.pricing.domain.model.Brand;
import com.altiacompany.pricing.infrastructure.adapter.input.rest.dto.BrandResponse;

/**
 * Mapper for Brand domain model and BrandResponse DTO.
 */
@Component
public class BrandRestMapper {

    public BrandResponse toResponse(Brand domain) {
        BrandResponse response = new BrandResponse();
        response.setId(domain.getId());
        response.setName(domain.getName());
        response.setCode(domain.getCode());
        response.setDescription(domain.getDescription());
        response.setActive(domain.getActive());
        return response;
    }

    public List<BrandResponse> toResponse(List<Brand> domainList) {
        return domainList.stream().map(this::toResponse).toList();
    }
}
