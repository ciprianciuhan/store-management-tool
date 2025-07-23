package com.ciprian.store_management_tool.service;

import com.ciprian.store_management_tool.model.Product;
import com.ciprian.store_management_tool.repository.ProductRepository;
import org.hibernate.annotations.CurrentTimestamp;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.sql.Time;
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

}
