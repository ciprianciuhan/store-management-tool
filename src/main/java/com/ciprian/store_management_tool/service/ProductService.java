package com.ciprian.store_management_tool.service;

import com.ciprian.store_management_tool.model.Product;
import com.ciprian.store_management_tool.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Product save(Product product) {
        log.info("Saving product with barcode: {}", product.getBarcode());
        product.setCreatedAt(LocalDateTime.now());
        return repository.save(product);
    }

    public Optional<Product> findById(String barcode) {
        log.info("Returning product with barcode: {}", barcode);
        return repository.findById(barcode);
    }

    public List<Product> findAll() {
        log.info("Showing all products");
        return repository.findAll();
    }

    public void deleteById(String barcode) {
        log.info("Deleting product with barcode: {}", barcode);
        repository.deleteById(barcode);
    }

    public Optional<Product> updatePrice(String barcode, BigDecimal newPrice) {
        log.info("Updating price for product with barcode: {}", barcode);
        return repository.findById(barcode)
                .map(product -> {
                    product.setPrice(newPrice);
                    return repository.save(product);
                });
    }

}
