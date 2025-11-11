package com.corep.productcatalog.inventory.repository;

import com.corep.productcatalog.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Inventory Repository
 * In microservices: this would be in a separate Inventory Service
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    boolean existsByProductId(Long productId);

    /**
     * Find low stock items (native SQL query)
     * In microservices: this would be a dedicated endpoint
     */
    @Query(value = "SELECT * FROM inventory WHERE (stock_quantity - reserved_quantity) < :threshold", nativeQuery = true)
    List<Inventory> findLowStockItems(@Param("threshold") Integer threshold);

    /**
     * Find out of stock items
     */
    @Query(value = "SELECT * FROM inventory WHERE (stock_quantity - reserved_quantity) <= 0", nativeQuery = true)
    List<Inventory> findOutOfStockItems();
}

