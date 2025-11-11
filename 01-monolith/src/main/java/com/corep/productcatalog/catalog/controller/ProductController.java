/*
 * Copyright (c) 2024-2025 Daniele Bortoluzzi
 * 
 * Master di I Livello in Cloud Computing
 * Università degli Studi di Torino - COREP
 * 
 * This project is part of the Cloud Computing Master's program.
 * Educational project for Cloud Computing Course.
 * 
 * MIT License - see LICENSE file for details
 */
package com.corep.productcatalog.catalog.controller;

import com.corep.productcatalog.catalog.dto.ProductDTO;
import com.corep.productcatalog.catalog.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product Controller
 * Manages product catalog endpoints
 * 
 * In microservices: this would be in Product Catalog Service
 * Current endpoints: /api/v1/products/*
 * Future endpoints: http://product-catalog-service:8081/api/v1/products/*
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    /**
     * Get all products with optional filtering and pagination
     * Example: /api/v1/products?category=Electronics&page=0&size=10&sort=price,asc
     */
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        // If pagination parameters are provided, use pagination
        if (page >= 0 && size > 0) {
            Sort sort = sortDir.equalsIgnoreCase("desc") 
                    ? Sort.by(sortBy).descending() 
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            if (category != null && !category.isEmpty()) {
                Page<ProductDTO> products = productService.getProductsByCategory(category, pageable);
                return ResponseEntity.ok(products);
            } else if (minPrice != null && maxPrice != null) {
                Page<ProductDTO> products = productService.getProductsByPriceRange(minPrice, maxPrice, pageable);
                return ResponseEntity.ok(products);
            } else {
                Page<ProductDTO> products = productService.getAllProducts(pageable);
                return ResponseEntity.ok(products);
            }
        }

        // Otherwise, return all results (backward compatibility)
        List<ProductDTO> products;
        if (category != null && !category.isEmpty()) {
            products = productService.getProductsByCategory(category);
        } else {
            products = productService.getAllProducts();
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Get products by category ordered by price (uses JPQL query)
     */
    @GetMapping("/category/{category}/ordered-by-price")
    public ResponseEntity<List<ProductDTO>> getProductsByCategoryOrderedByPrice(
            @PathVariable String category) {
        List<ProductDTO> products = productService.getProductsByCategoryOrderedByPrice(category);
        return ResponseEntity.ok(products);
    }

    /**
     * Get category statistics (uses native SQL query with aggregation)
     */
    @GetMapping("/statistics/categories")
    public ResponseEntity<List<Object[]>> getCategoryStatistics() {
        List<Object[]> statistics = productService.getCategoryStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get products by price range (uses native SQL query)
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<ProductDTO>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        List<ProductDTO> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}

