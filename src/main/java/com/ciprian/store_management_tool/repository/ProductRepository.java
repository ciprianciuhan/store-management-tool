package com.ciprian.store_management_tool.repository;

import com.ciprian.store_management_tool.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
