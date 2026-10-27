package com.altiacompany.pricing.infrastructure.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link BrandEntity}.
 */
public interface SpringDataBrandRepository extends JpaRepository<BrandEntity, Long> {
}
