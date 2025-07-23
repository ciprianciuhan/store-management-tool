package com.ciprian.store_management_tool.controller;

import com.ciprian.store_management_tool.dto.UpdatePriceRequest;
import com.ciprian.store_management_tool.model.Product;
import com.ciprian.store_management_tool.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return ResponseEntity.ok(service.save(product));
    }

    @GetMapping("/{barcode}")
    public ResponseEntity<Product> get(@PathVariable String barcode) {
        return service.findById(barcode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Product> getAll() {
        return service.findAll();
    }

    @DeleteMapping("/{barcode}")
    public ResponseEntity<Void> delete(@PathVariable String barcode) {
        service.deleteById(barcode);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{barcode}/price")
    public ResponseEntity<Product> updatePrice(@PathVariable String barcode, @RequestBody UpdatePriceRequest request) {
        return service.updatePrice(barcode, request.price())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
