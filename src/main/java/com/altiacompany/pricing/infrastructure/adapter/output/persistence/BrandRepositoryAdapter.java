package com.altiacompany.pricing.infrastructure.adapter.output.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.altiacompany.pricing.application.port.output.BrandRepository;
import com.altiacompany.pricing.domain.model.Brand;

@Repository
public class BrandRepositoryAdapter implements BrandRepository {

    private final SpringDataBrandRepository repository;
    private final BrandPersistenceMapper mapper;

    public BrandRepositoryAdapter(SpringDataBrandRepository repository,
            BrandPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Brand> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Brand> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}
