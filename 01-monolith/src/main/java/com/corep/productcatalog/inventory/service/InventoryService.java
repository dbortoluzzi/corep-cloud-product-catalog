package com.corep.productcatalog.inventory.service;

import com.corep.productcatalog.inventory.dto.InventoryDTO;
import com.corep.productcatalog.inventory.entity.Inventory;
import com.corep.productcatalog.inventory.exception.InventoryNotFoundException;
import com.corep.productcatalog.inventory.exception.InsufficientStockException;
import com.corep.productcatalog.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Inventory Service
 * Manages stock quantities and availability
 * 
 * In modular monolith: trusts that ProductService validates product existence before calling this service.
 * The product_id foreign key constraint in the database ensures referential integrity.
 * 
 * In microservices architecture:
 * - This service would be extracted to a separate Inventory Service
 * - Would communicate with Product Catalog Service via Feign Client or Events
 * - Would have its own database
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    /**
     * Create inventory for a product
     * 
     * Note: Product validation is handled by ProductService before calling this method.
     * The product_id foreign key constraint ensures referential integrity at the database level.
     * 
     * @param productId The product ID (must exist in products table - validated by caller)
     * @param initialStock Initial stock quantity
     * @return Created inventory DTO
     */
    public InventoryDTO createInventory(Long productId, Integer initialStock) {
        // Check if inventory already exists
        if (inventoryRepository.existsByProductId(productId)) {
            throw new IllegalArgumentException("Inventory already exists for product: " + productId);
        }

        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setStockQuantity(initialStock);
        inventory.setReservedQuantity(0);

        Inventory saved = inventoryRepository.save(inventory);
        log.info("Created inventory for product {} with stock {}", productId, initialStock);
        return convertToDTO(saved);
    }

    /**
     * Get inventory for a product
     * In microservices: this would be a REST endpoint
     */
    @Transactional(readOnly = true)
    public InventoryDTO getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product: " + productId));
        return convertToDTO(inventory);
    }

    /**
     * Update stock quantity
     * In microservices: this would be a REST endpoint
     */
    public InventoryDTO updateStock(Long productId, Integer newStockQuantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product: " + productId));

        inventory.setStockQuantity(newStockQuantity);
        Inventory updated = inventoryRepository.save(inventory);
        log.info("Updated stock for product {} to {}", productId, newStockQuantity);
        return convertToDTO(updated);
    }

    /**
     * Reserve stock (for orders)
     * In microservices: this would be called by Order Service
     */
    public InventoryDTO reserveStock(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product: " + productId));

        int available = inventory.getAvailableQuantity();
        if (available < quantity) {
            throw new InsufficientStockException(
                    String.format("Insufficient stock. Available: %d, Requested: %d", available, quantity));
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        Inventory updated = inventoryRepository.save(inventory);
        log.info("Reserved {} units for product {}", quantity, productId);
        return convertToDTO(updated);
    }

    /**
     * Release reserved stock (when order is cancelled)
     * In microservices: this would be called by Order Service
     */
    public InventoryDTO releaseStock(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getReservedQuantity() < quantity) {
            throw new IllegalArgumentException(
                    String.format("Cannot release more than reserved. Reserved: %d, Requested: %d",
                            inventory.getReservedQuantity(), quantity));
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        Inventory updated = inventoryRepository.save(inventory);
        log.info("Released {} units for product {}", quantity, productId);
        return convertToDTO(updated);
    }

    /**
     * Get low stock items
     * In microservices: this would be a REST endpoint
     */
    @Transactional(readOnly = true)
    public List<InventoryDTO> getLowStockItems(Integer threshold) {
        return inventoryRepository.findLowStockItems(threshold)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get out of stock items
     */
    @Transactional(readOnly = true)
    public List<InventoryDTO> getOutOfStockItems() {
        return inventoryRepository.findOutOfStockItems()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Delete inventory (when product is deleted)
     * In microservices: this would be triggered by ProductDeletedEvent
     */
    public void deleteInventory(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product: " + productId));
        inventoryRepository.delete(inventory);
        log.info("Deleted inventory for product {}", productId);
    }

    private InventoryDTO convertToDTO(Inventory inventory) {
        InventoryDTO dto = new InventoryDTO();
        dto.setId(inventory.getId());
        dto.setProductId(inventory.getProductId());
        dto.setStockQuantity(inventory.getStockQuantity());
        dto.setReservedQuantity(inventory.getReservedQuantity());
        dto.setAvailableQuantity(inventory.getAvailableQuantity());
        dto.setLastUpdated(inventory.getLastUpdated());
        return dto;
    }
}

