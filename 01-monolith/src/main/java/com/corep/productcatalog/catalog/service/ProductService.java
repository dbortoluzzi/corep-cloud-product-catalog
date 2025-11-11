package com.corep.productcatalog.catalog.service;

import com.corep.productcatalog.catalog.dto.ProductDTO;
import com.corep.productcatalog.catalog.entity.Product;
import com.corep.productcatalog.catalog.exception.ProductNotFoundException;
import com.corep.productcatalog.catalog.repository.ProductRepository;
import com.corep.productcatalog.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Product Service
 * Manages product catalog information (name, description, price, category)
 * 
 * In modular monolith: coordinates with InventoryService for inventory creation/deletion
 * In microservices architecture:
 * - This service would be extracted to Product Catalog Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    /**
     * Creates a new product and automatically creates inventory
     * @Transactional ensures atomicity - if any error occurs, the transaction is rolled back
     * 
     * In modular monolith: calls InventoryService to create inventory (synchronous)
     * In microservices: would publish ProductCreatedEvent for Inventory Service (asynchronous)
     */
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        
        // Automatically create inventory for the new product (modular monolith pattern)
        // In microservices, this would be replaced by publishing ProductCreatedEvent
        try {
            inventoryService.createInventory(savedProduct.getId(), 0); // Initial stock: 0
            log.info("Created inventory for product {}", savedProduct.getId());
        } catch (Exception e) {
            log.error("Failed to create inventory for product {}: {}", savedProduct.getId(), e.getMessage());
            // In a real scenario, you might want to rollback the product creation
            // For now, we log the error but continue (inventory can be created later)
        }
        
        return convertToDTO(savedProduct);
    }

    /**
     * Retrieves all products
     * @Transactional(readOnly = true) optimizes for read-only operations
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves products with pagination support
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return convertToDTO(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves products by category with pagination
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Uses JPQL query to find products by category ordered by price
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategoryOrderedByPrice(String category) {
        return productRepository.findProductsByCategoryOrderedByPrice(category)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Uses native SQL query to get category statistics
     */
    @Transactional(readOnly = true)
    public List<Object[]> getCategoryStatistics() {
        return productRepository.getCategoryStatistics();
    }

    /**
     * Uses native SQL query to find products by price range
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findProductsByPriceRange(minPrice, maxPrice)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Uses pagination to find products by price range
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable)
                .map(this::convertToDTO);
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setCategory(productDTO.getCategory());

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    /**
     * Deletes a product and automatically deletes associated inventory
     * In modular monolith: calls InventoryService to delete inventory (synchronous)
     * In microservices: would publish ProductDeletedEvent for Inventory Service (asynchronous)
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        
        // Automatically delete inventory for the product (modular monolith pattern)
        // In microservices, this would be replaced by publishing ProductDeletedEvent
        try {
            inventoryService.deleteInventory(id);
            log.info("Deleted inventory for product {}", id);
        } catch (Exception e) {
            log.warn("Inventory not found or already deleted for product {}: {}", id, e.getMessage());
            // Continue with product deletion even if inventory doesn't exist
        }
        
        productRepository.deleteById(id);
        log.info("Deleted product {}", id);
    }

    private Product convertToEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        return product;
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setCategory(product.getCategory());
        productDTO.setCreatedAt(product.getCreatedAt());
        productDTO.setUpdatedAt(product.getUpdatedAt());
        return productDTO;
    }
}

