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

import com.altiacompany.pricing.application.port.input.GetProductsQuery;
import com.altiacompany.pricing.application.port.output.ProductRepository;
import com.altiacompany.pricing.domain.model.Product;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class GetProductsServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetProductsService getProductsService;

    @Test
    void shouldReturnAllProductsWhenQueryHasNoFilters() {
        // Given
        Product product1 = new Product(35455L, "Standard Product", "Seed product referenced by prices");
        Product product2 = new Product(1L, "Basic Product", "Entry-level product");
        Product product3 = new Product(2L, "Premium Product", "High-end product");
        List<Product> expectedProducts = Arrays.asList(product1, product2, product3);
        when(productRepository.findAll()).thenReturn(expectedProducts);
        GetProductsQuery query = GetProductsQuery.builder().build();

        // When
        List<Product> result = getProductsService.getAllProducts(query);

        // Then
        assertThat(result).isEqualTo(expectedProducts);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void shouldFilterProductsByNameCaseInsensitive() {
        // Given
        Product product1 = new Product(35455L, "Standard Product", "Seed product referenced by prices");
        Product product2 = new Product(1L, "Basic Product", "Entry-level product");
        Product product3 = new Product(2L, "Premium Product", "High-end product");
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2, product3));
        GetProductsQuery query = GetProductsQuery.builder().q("standard").build();

        // When
        List<Product> result = getProductsService.getAllProducts(query);

        // Then
        assertThat(result).containsExactly(product1);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void shouldFilterProductsByDescription() {
        // Given
        Product product1 = new Product(35455L, "Standard Product", "Seed product referenced by prices");
        Product product2 = new Product(1L, "Basic Product", "Entry-level product");
        Product product3 = new Product(2L, "Premium Product", "High-end product");
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2, product3));
        GetProductsQuery query = GetProductsQuery.builder().q("entry-level").build();

        // When
        List<Product> result = getProductsService.getAllProducts(query);

        // Then
        assertThat(result).containsExactly(product2);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsMatchQuery() {
        // Given
        Product product1 = new Product(35455L, "Standard Product", "Seed product referenced by prices");
        when(productRepository.findAll()).thenReturn(List.of(product1));
        GetProductsQuery query = GetProductsQuery.builder().q("unknown").build();

        // When
        List<Product> result = getProductsService.getAllProducts(query);

        // Then
        assertThat(result).isEmpty();
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void shouldNotFilterProductsWhenQueryIsBlank() {
        // Given
        Product product1 = new Product(35455L, "Standard Product", "Seed product referenced by prices");
        Product product2 = new Product(1L, "Basic Product", "Entry-level product");
        Product product3 = new Product(2L, "Premium Product", "High-end product");
        List<Product> expectedProducts = Arrays.asList(product1, product2, product3);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // When
        List<Product> resultWithEmptyQuery = getProductsService.getAllProducts(GetProductsQuery.builder().q("").build());
        List<Product> resultWithWhitespaceQuery = getProductsService.getAllProducts(GetProductsQuery.builder().q("   ").build());

        // Then
        assertThat(resultWithEmptyQuery).isEqualTo(expectedProducts);
        assertThat(resultWithWhitespaceQuery).isEqualTo(expectedProducts);
        verify(productRepository, times(2)).findAll();
    }

    @Test
    void shouldHandleNullDescriptionWithoutThrowing() {
        // Given
        Product product1 = new Product(35455L, "Standard Product", "Seed product referenced by prices");
        Product product2 = new Product(2L, "Premium Product", null);
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        GetProductsQuery query = GetProductsQuery.builder().q("premium").build();

        // When
        List<Product> result = getProductsService.getAllProducts(query);

        // Then
        assertThat(result).containsExactly(product2);
        verify(productRepository, times(1)).findAll();
    }
}
