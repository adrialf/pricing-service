package com.altiacompany.pricing.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.altiacompany.pricing.application.port.input.GetBrandsQuery;
import com.altiacompany.pricing.application.port.output.BrandRepository;
import com.altiacompany.pricing.domain.model.Brand;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class GetBrandsServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private GetBrandsService getBrandsService;

    @Test
    void shouldReturnAllBrandsWhenQueryHasNoFilters() {
        // Given
        Brand brand1 = new Brand(1L, "Renault", "RENAULT", "Renault brand", true);
        Brand brand2 = new Brand(2L, "Opel", "OPEL", "Opel brand", true);
        List<Brand> expectedBrands = Arrays.asList(brand1, brand2);
        when(brandRepository.findAll()).thenReturn(expectedBrands);
        GetBrandsQuery query = GetBrandsQuery.builder().build();

        // When
        List<Brand> result = getBrandsService.getAllBrands(query);

        // Then
        assertThat(result).isEqualTo(expectedBrands);
        verify(brandRepository, times(1)).findAll();
    }

    @Test
    void shouldFilterBrandsById() {
        // Given
        Brand brand1 = new Brand(1L, "Renault", "RENAULT", "Renault brand", true);
        Brand brand2 = new Brand(2L, "Opel", "OPEL", "Opel brand", true);
        when(brandRepository.findAll()).thenReturn(Arrays.asList(brand1, brand2));
        GetBrandsQuery query = GetBrandsQuery.builder().id(2L).build();

        // When
        List<Brand> result = getBrandsService.getAllBrands(query);

        // Then
        assertThat(result).containsExactly(brand2);
        verify(brandRepository, times(1)).findAll();
    }

    @Test
    void shouldFilterBrandsByActive() {
        // Given
        Brand brand1 = new Brand(1L, "Renault", "RENAULT", "Renault brand", true);
        Brand brand2 = new Brand(2L, "Opel", "OPEL", "Opel brand", false);
        when(brandRepository.findAll()).thenReturn(Arrays.asList(brand1, brand2));
        GetBrandsQuery query = GetBrandsQuery.builder().active(true).build();

        // When
        List<Brand> result = getBrandsService.getAllBrands(query);

        // Then
        assertThat(result).containsExactly(brand1);
        verify(brandRepository, times(1)).findAll();
    }

    @Test
    void shouldFilterBrandsByQuery() {
        // Given
        Brand brand1 = new Brand(1L, "Renault", "RENAULT", "Renault brand", true);
        Brand brand2 = new Brand(2L, "Opel", "OPEL", "Opel brand", true);
        when(brandRepository.findAll()).thenReturn(Arrays.asList(brand1, brand2));
        GetBrandsQuery query = GetBrandsQuery.builder().q("renault").build();

        // When
        List<Brand> result = getBrandsService.getAllBrands(query);

        // Then
        assertThat(result).containsExactly(brand1);
        verify(brandRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoBrandsMatchFilters() {
        // Given
        Brand brand1 = new Brand(1L, "Renault", "RENAULT", "Renault brand", true);
        when(brandRepository.findAll()).thenReturn(List.of(brand1));
        GetBrandsQuery query = GetBrandsQuery.builder().id(999L).build();

        // When
        List<Brand> result = getBrandsService.getAllBrands(query);

        // Then
        assertThat(result).isEmpty();
        verify(brandRepository, times(1)).findAll();
    }
}
