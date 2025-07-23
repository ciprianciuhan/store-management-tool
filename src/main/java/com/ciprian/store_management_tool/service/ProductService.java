package com.ciprian.store_management_tool.service;

import com.ciprian.store_management_tool.model.Product;
import com.ciprian.store_management_tool.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product save(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        return repository.save(product);
    }

    public Optional<Product> findById(String barcode) {
        return repository.findById(barcode);
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public void deleteById(String barcode) {
        repository.deleteById(barcode);
    }

    public Optional<Product> updatePrice(String barcode, BigDecimal newPrice) {
        return repository.findById(barcode)
                .map(product -> {
                    product.setPrice(newPrice);
                    return repository.save(product);
                });
    }

}
