package com.corep.productcatalog.shared.config;

import com.corep.productcatalog.catalog.entity.Product;
import com.corep.productcatalog.catalog.repository.ProductRepository;
import com.corep.productcatalog.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Data initializer for development
 * Runs only in 'dev' profile to populate sample data
 * Demonstrates CommandLineRunner interface
 * 
 * In microservices: this would be split into separate initializers
 * - Product Catalog Service initializer
 * - Inventory Service initializer (triggered by events)
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            log.info("Initializing sample data...");

            // Create products
            Product product1 = createAndSaveProduct("Laptop Dell XPS 15", 
                    "High-performance laptop with 4K display", 
                    new BigDecimal("1299.99"), 
                    "Electronics");
            inventoryService.createInventory(product1.getId(), 25);

            Product product2 = createAndSaveProduct("iPhone 15 Pro", 
                    "Latest iPhone with A17 Pro chip", 
                    new BigDecimal("999.00"), 
                    "Electronics");
            inventoryService.createInventory(product2.getId(), 50);

            Product product3 = createAndSaveProduct("Spring in Action", 
                    "Comprehensive guide to Spring Framework", 
                    new BigDecimal("49.99"), 
                    "Books");
            inventoryService.createInventory(product3.getId(), 100);

            Product product4 = createAndSaveProduct("Wireless Mouse", 
                    "Ergonomic wireless mouse", 
                    new BigDecimal("29.99"), 
                    "Accessories");
            inventoryService.createInventory(product4.getId(), 200);

            Product product5 = createAndSaveProduct("Mechanical Keyboard", 
                    "RGB mechanical keyboard with Cherry MX switches", 
                    new BigDecimal("149.99"), 
                    "Accessories");
            inventoryService.createInventory(product5.getId(), 30);

            Product product6 = createAndSaveProduct("Monitor 27\" 4K", 
                    "Ultra HD 4K monitor with HDR", 
                    new BigDecimal("399.99"), 
                    "Electronics");
            inventoryService.createInventory(product6.getId(), 15);

            log.info("Sample data initialized: {} products with inventory", productRepository.count());
        } else {
            log.info("Database already contains data, skipping initialization");
        }
    }

    private Product createAndSaveProduct(String name, String description, BigDecimal price, String category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        return productRepository.save(product);
    }
}

