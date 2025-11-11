package com.corep.productcatalog.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Inventory entity - manages stock quantities
 * This will become a separate microservice in the future
 * Currently in the monolith but in a separate package for easy extraction
 */
@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Reference to product ID
     * In microservices: this would be a foreign key reference
     * For now: we'll validate product exists
     */
    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    protected void onCreate() {
        lastUpdated = LocalDateTime.now();
        if (reservedQuantity == null) {
            reservedQuantity = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    /**
     * Get available stock (total - reserved)
     */
    public Integer getAvailableQuantity() {
        return stockQuantity - reservedQuantity;
    }

    /**
     * Check if product is in stock
     */
    public boolean isInStock() {
        return getAvailableQuantity() > 0;
    }
}

