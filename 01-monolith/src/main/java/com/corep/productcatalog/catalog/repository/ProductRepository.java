package com.corep.productcatalog.catalog.repository;

import com.corep.productcatalog.catalog.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product Repository
 * In microservices: this would be in Product Catalog Service
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Spring Data JPA Query Methods (derived queries)
    List<Product> findByCategory(String category);

    List<Product> findByNameContainingIgnoreCase(String name);

    boolean existsById(Long id);

    // Pagination support
    Page<Product> findByCategory(String category, Pageable pageable);

    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // JPQL Query with sorting
    @Query("SELECT p FROM Product p WHERE p.category = :category ORDER BY p.price ASC")
    List<Product> findProductsByCategoryOrderedByPrice(@Param("category") String category);

    // Native SQL Query with aggregation
    @Query(value = "SELECT category, COUNT(*) as count, AVG(price) as avg_price FROM products GROUP BY category", nativeQuery = true)
    List<Object[]> getCategoryStatistics();

    // Native SQL Query for price range search
    @Query(value = "SELECT * FROM products WHERE price BETWEEN :minPrice AND :maxPrice ORDER BY price DESC", nativeQuery = true)
    List<Product> findProductsByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
}

