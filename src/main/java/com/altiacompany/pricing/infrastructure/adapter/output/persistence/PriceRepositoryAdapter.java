package com.altiacompany.pricing.infrastructure.adapter.output.persistence;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.altiacompany.pricing.application.port.output.PriceRepository;
import com.altiacompany.pricing.domain.model.Price;

@Repository
public class PriceRepositoryAdapter implements PriceRepository {

    private final SpringDataPriceRepository repository;

    private final PricePersistenceMapper mapper;

    public PriceRepositoryAdapter(SpringDataPriceRepository repository,
            PricePersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Price> findByApplicationDateAndProductIdAndBrandId(
            Instant applicationDate, Long productId, Long brandId) {
        // @formatter:off
        return repository.findByApplicationDateAndProductIdAndBrandId(
                        applicationDate, productId, brandId)
                .stream()
                .map(mapper::toDomain)
                .toList();
        // @formatter:on
    }
}
