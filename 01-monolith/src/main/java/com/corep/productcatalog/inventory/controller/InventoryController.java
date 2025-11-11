package com.corep.productcatalog.inventory.controller;

import com.corep.productcatalog.inventory.dto.InventoryDTO;
import com.corep.productcatalog.inventory.service.InventoryService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Inventory Controller
 * Manages inventory/stock endpoints
 * 
 * In microservices: this would be in a separate Inventory Service
 * Current endpoints: /api/v1/inventory/*
 * Future endpoints: http://inventory-service:8082/api/v1/inventory/*
 */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Create inventory for a product
     * In microservices: this would be triggered by ProductCreatedEvent
     */
    @PostMapping("/product/{productId}")
    public ResponseEntity<InventoryDTO> createInventory(
            @PathVariable Long productId,
            @RequestParam @NotNull @Min(0) Integer initialStock) {
        InventoryDTO inventory = inventoryService.createInventory(productId, initialStock);
        return new ResponseEntity<>(inventory, HttpStatus.CREATED);
    }

    /**
     * Get inventory for a product
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryDTO> getInventoryByProductId(@PathVariable Long productId) {
        InventoryDTO inventory = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(inventory);
    }

    /**
     * Update stock quantity
     */
    @PutMapping("/product/{productId}/stock")
    public ResponseEntity<InventoryDTO> updateStock(
            @PathVariable Long productId,
            @RequestParam @NotNull @Min(0) Integer stockQuantity) {
        InventoryDTO inventory = inventoryService.updateStock(productId, stockQuantity);
        return ResponseEntity.ok(inventory);
    }

    /**
     * Reserve stock (for orders)
     * In microservices: this would be called by Order Service
     */
    @PostMapping("/product/{productId}/reserve")
    public ResponseEntity<InventoryDTO> reserveStock(
            @PathVariable Long productId,
            @RequestParam @NotNull @Min(1) Integer quantity) {
        InventoryDTO inventory = inventoryService.reserveStock(productId, quantity);
        return ResponseEntity.ok(inventory);
    }

    /**
     * Release reserved stock (when order is cancelled)
     * In microservices: this would be called by Order Service
     */
    @PostMapping("/product/{productId}/release")
    public ResponseEntity<InventoryDTO> releaseStock(
            @PathVariable Long productId,
            @RequestParam @NotNull @Min(1) Integer quantity) {
        InventoryDTO inventory = inventoryService.releaseStock(productId, quantity);
        return ResponseEntity.ok(inventory);
    }

    /**
     * Get low stock items
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryDTO>> getLowStockItems(
            @RequestParam(defaultValue = "10") Integer threshold) {
        List<InventoryDTO> items = inventoryService.getLowStockItems(threshold);
        return ResponseEntity.ok(items);
    }

    /**
     * Get out of stock items
     */
    @GetMapping("/out-of-stock")
    public ResponseEntity<List<InventoryDTO>> getOutOfStockItems() {
        List<InventoryDTO> items = inventoryService.getOutOfStockItems();
        return ResponseEntity.ok(items);
    }

    /**
     * Delete inventory (when product is deleted)
     * In microservices: this would be triggered by ProductDeletedEvent
     */
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long productId) {
        inventoryService.deleteInventory(productId);
        return ResponseEntity.noContent().build();
    }
}

