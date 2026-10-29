package com.altiacompany.pricing.infrastructure.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link ProductEntity}.
 */
public interface SpringDataProductRepository extends JpaRepository<ProductEntity, Long> {
}
