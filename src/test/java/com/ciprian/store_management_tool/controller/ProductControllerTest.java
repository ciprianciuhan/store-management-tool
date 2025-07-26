package com.ciprian.store_management_tool.controller;

import com.ciprian.store_management_tool.dto.UpdatePriceRequest;
import com.ciprian.store_management_tool.model.Product;
import com.ciprian.store_management_tool.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController controller;

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
    void create_ShouldReturnSavedProduct() {
        // Arrange
        when(productService.save(any(Product.class))).thenReturn(product1);

        // Act
        ResponseEntity<Product> response = controller.create(product1);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(product1);
        verify(productService).save(product1);
    }

    @Test
    void get_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        when(productService.findById("123456789")).thenReturn(Optional.of(product1));

        // Act
        ResponseEntity<Product> response = controller.get("123456789");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(product1);
        verify(productService).findById("123456789");
    }

    @Test
    void get_WhenProductDoesNotExist_ShouldReturnNotFound() {
        // Arrange
        when(productService.findById("nonexistent")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Product> response = controller.get("nonexistent");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(productService).findById("nonexistent");
    }

    @Test
    void getAll_ShouldReturnAllProducts() {
        // Arrange
        List<Product> products = Arrays.asList(product1, product2);
        when(productService.findAll()).thenReturn(products);

        // Act
        List<Product> result = controller.getAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(product1, product2);
        verify(productService).findAll();
    }

    @Test
    void delete_ShouldReturnNoContent() {
        // Arrange - nothing to do

        // Act
        ResponseEntity<Void> response = controller.delete("123456789");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(productService).deleteById("123456789");
    }

    @Test
    void updatePrice_WhenProductExists_ShouldReturnUpdatedProduct() {
        // Arrange
        BigDecimal newPrice = new BigDecimal("24.99");
        UpdatePriceRequest request = new UpdatePriceRequest(newPrice);

        Product updatedProduct = Product.builder()
                .barcode("123456789")
                .name("Test Product 1")
                .price(newPrice)
                .quantity(10)
                .createdAt(product1.getCreatedAt())
                .build();

        when(productService.updatePrice("123456789", newPrice)).thenReturn(Optional.of(updatedProduct));

        // Act
        ResponseEntity<Product> response = controller.updatePrice("123456789", request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedProduct);
        assertThat(response.getBody().getPrice()).isEqualByComparingTo(newPrice);
        verify(productService).updatePrice("123456789", newPrice);
    }

    @Test
    void updatePrice_WhenProductDoesNotExist_ShouldReturnNotFound() {
        // Arrange
        BigDecimal newPrice = new BigDecimal("24.99");
        UpdatePriceRequest request = new UpdatePriceRequest(newPrice);

        when(productService.updatePrice("nonexistent", newPrice)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Product> response = controller.updatePrice("nonexistent", request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(productService).updatePrice("nonexistent", newPrice);
    }
}
