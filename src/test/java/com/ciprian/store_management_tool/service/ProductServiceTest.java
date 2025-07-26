package com.ciprian.store_management_tool.service;

import com.ciprian.store_management_tool.model.Product;
import com.ciprian.store_management_tool.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        LocalDateTime fixedTime = LocalDateTime.of(2025, 7, 26, 10, 0);

        product1 = Product.builder()
                .barcode("123456789")
                .name("Test Product 1")
                .price(new BigDecimal("19.99"))
                .quantity(10)
                .createdAt(fixedTime)
                .build();

        product2 = Product.builder()
                .barcode("987654321")
                .name("Test Product 2")
                .price(new BigDecimal("29.99"))
                .quantity(5)
                .createdAt(fixedTime)
                .build();
    }

    @Test
    void save_ShouldSetCreatedAtAndSaveProduct() {
        // Arrange
        Product productToSave = Product.builder()
                .barcode("123456789")
                .name("Test Product")
                .price(new BigDecimal("19.99"))
                .quantity(10)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // Act
        Product savedProduct = productService.save(productToSave);

        // Assert
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getCreatedAt()).isNotNull();
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void findById_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findById("123456789")).thenReturn(Optional.of(product1));

        // Act
        Optional<Product> foundProduct = productService.findById("123456789");

        // Assert
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getBarcode()).isEqualTo("123456789");
        verify(productRepository).findById("123456789");
    }

    @Test
    void findById_WhenProductDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(productRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<Product> foundProduct = productService.findById("nonexistent");

        // Assert
        assertThat(foundProduct).isEmpty();
        verify(productRepository).findById("nonexistent");
    }

    @Test
    void findAll_ShouldReturnAllProducts() {
        // Arrange
        List<Product> productList = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(productList);

        // Act
        List<Product> foundProducts = productService.findAll();

        // Assert
        assertThat(foundProducts).hasSize(2);
        assertThat(foundProducts).containsExactly(product1, product2);
        verify(productRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepositoryDeleteById() {
        // Act
        productService.deleteById("123456789");

        // Assert
        verify(productRepository).deleteById("123456789");
    }

    @Test
    void updatePrice_WhenProductExists_ShouldUpdateAndReturnProduct() {
        // Arrange
        BigDecimal newPrice = new BigDecimal("24.99");
        Product updatedProduct = Product.builder()
                .barcode("123456789")
                .name("Test Product 1")
                .price(newPrice)
                .quantity(10)
                .createdAt(product1.getCreatedAt())
                .build();

        when(productRepository.findById("123456789")).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Optional<Product> result = productService.updatePrice("123456789", newPrice);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getPrice()).isEqualByComparingTo(newPrice);
        verify(productRepository).findById("123456789");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updatePrice_WhenProductDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        BigDecimal newPrice = new BigDecimal("24.99");
        when(productRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.updatePrice("nonexistent", newPrice);

        // Assert
        assertThat(result).isEmpty();
        verify(productRepository).findById("nonexistent");
        verify(productRepository, never()).save(any(Product.class));
    }
}
