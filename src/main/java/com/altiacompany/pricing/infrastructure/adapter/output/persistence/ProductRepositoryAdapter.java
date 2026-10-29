package com.altiacompany.pricing.infrastructure.adapter.output.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.altiacompany.pricing.application.port.output.ProductRepository;
import com.altiacompany.pricing.domain.model.Product;

@Repository
public class ProductRepositoryAdapter implements ProductRepository {

    private final SpringDataProductRepository repository;
    private final ProductPersistenceMapper mapper;

    public ProductRepositoryAdapter(SpringDataProductRepository repository,
            ProductPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
}
