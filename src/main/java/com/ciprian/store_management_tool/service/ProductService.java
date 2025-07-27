package com.ciprian.store_management_tool.service;

import com.ciprian.store_management_tool.dto.ProductCreatedEvent;
import com.ciprian.store_management_tool.exception.DuplicateProductException;
import com.ciprian.store_management_tool.model.Product;
import com.ciprian.store_management_tool.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ProductEventPublisher eventPublisher;

    public Product save(Product product) {
        log.info("Saving product with barcode: {}", product.getBarcode());

        if (repository.existsById(product.getBarcode())) {
            log.warn("Attempt to create duplicate product with barcode: {}", product.getBarcode());
            throw new DuplicateProductException(product.getBarcode());
        }

        product.setCreatedAt(LocalDateTime.now());
        Product saved = repository.save(product);
        eventPublisher.publish(new ProductCreatedEvent(saved.getBarcode(), saved.getName(), saved.getPrice()));
        return saved;
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
