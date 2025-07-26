package com.ciprian.store_management_tool.controller;

import com.ciprian.store_management_tool.config.TestConfig;
import com.ciprian.store_management_tool.dto.UpdatePriceRequest;
import com.ciprian.store_management_tool.model.Product;
import com.ciprian.store_management_tool.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
@ActiveProfiles("test")
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        product1 = Product.builder()
                .barcode("123456789")
                .name("Test Product 1")
                .price(new BigDecimal("19.99"))
                .quantity(10)
                .build();

        product2 = Product.builder()
                .barcode("987654321")
                .name("Test Product 2")
                .price(new BigDecimal("29.99"))
                .quantity(5)
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void create_WithAdminRole_ShouldCreateProduct() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.barcode").value(product1.getBarcode()))
                .andExpect(jsonPath("$.name").value(product1.getName()))
                .andExpect(jsonPath("$.price").value(product1.getPrice().doubleValue()))
                .andExpect(jsonPath("$.quantity").value(product1.getQuantity()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        assertThat(productRepository.findById(product1.getBarcode())).isPresent();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void create_WithUserRole_ShouldReturnForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("This action requires admin privileges"))
                .andExpect(jsonPath("$.code").value("ADMIN_PRIVILEGES_REQUIRED"));

        assertThat(productRepository.findById(product1.getBarcode())).isEmpty();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void get_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Arrange
        productRepository.save(product1);

        // Act & Assert
        mockMvc.perform(get("/products/{barcode}", product1.getBarcode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.barcode").value(product1.getBarcode()))
                .andExpect(jsonPath("$.name").value(product1.getName()))
                .andExpect(jsonPath("$.price").value(product1.getPrice().doubleValue()))
                .andExpect(jsonPath("$.quantity").value(product1.getQuantity()));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void get_WhenProductDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/products/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getAll_ShouldReturnAllProducts() throws Exception {
        // Arrange
        productRepository.saveAll(List.of(product1, product2));

        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].barcode").value(product1.getBarcode()))
                .andExpect(jsonPath("$[1].barcode").value(product2.getBarcode()));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void delete_WithAdminRole_ShouldDeleteProduct() throws Exception {
        // Arrange
        productRepository.save(product1);

        // Act & Assert
        mockMvc.perform(delete("/products/{barcode}", product1.getBarcode()))
                .andExpect(status().isNoContent());

        assertThat(productRepository.findById(product1.getBarcode())).isEmpty();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void delete_WithUserRole_ShouldReturnForbidden() throws Exception {
        // Arrange
        productRepository.save(product1);

        // Act & Assert
        mockMvc.perform(delete("/products/{barcode}", product1.getBarcode()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied to the requested resource"));

        assertThat(productRepository.findById(product1.getBarcode())).isPresent();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updatePrice_WithAdminRole_WhenProductExists_ShouldUpdatePrice() throws Exception {
        // Arrange
        productRepository.save(product1);
        BigDecimal newPrice = new BigDecimal("24.99");
        UpdatePriceRequest request = new UpdatePriceRequest(newPrice);

        // Act & Assert
        MvcResult result = mockMvc.perform(patch("/products/{barcode}/price", product1.getBarcode())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.barcode").value(product1.getBarcode()))
                .andExpect(jsonPath("$.price").value(newPrice.doubleValue()))
                .andReturn();

        Product updatedProduct = productRepository.findById(product1.getBarcode()).orElseThrow();
        assertThat(updatedProduct.getPrice()).isEqualByComparingTo(newPrice);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updatePrice_WithUserRole_ShouldReturnForbidden() throws Exception {
        // Arrange
        productRepository.save(product1);
        BigDecimal newPrice = new BigDecimal("24.99");
        UpdatePriceRequest request = new UpdatePriceRequest(newPrice);

        // Act & Assert
        mockMvc.perform(patch("/products/{barcode}/price", product1.getBarcode())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("This action requires admin privileges"))
                .andExpect(jsonPath("$.code").value("ADMIN_PRIVILEGES_REQUIRED"));

        Product updatedProduct = productRepository.findById(product1.getBarcode()).orElseThrow();
        assertThat(updatedProduct.getPrice()).isEqualByComparingTo(product1.getPrice());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updatePrice_WhenProductDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        BigDecimal newPrice = new BigDecimal("24.99");
        UpdatePriceRequest request = new UpdatePriceRequest(newPrice);

        // Act & Assert
        mockMvc.perform(patch("/products/nonexistent/price")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
