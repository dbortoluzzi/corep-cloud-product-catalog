package com.corep.productcatalog.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Inventory DTO
 * Used for transferring inventory data between layers
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDTO {

    private Long id;
    private Long productId;
    private Integer stockQuantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private LocalDateTime lastUpdated;
}

