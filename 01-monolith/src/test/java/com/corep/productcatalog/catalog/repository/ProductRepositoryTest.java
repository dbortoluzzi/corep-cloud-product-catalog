package com.corep.productcatalog.catalog.repository;

import com.corep.productcatalog.catalog.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setCategory("Electronics");
    }

    @Test
    void testSaveProduct() {
        // When
        Product saved = productRepository.save(testProduct);

        // Then
        assertNotNull(saved.getId());
        assertEquals(testProduct.getName(), saved.getName());
    }

    @Test
    void testFindById() {
        // Given
        Product saved = entityManager.persistAndFlush(testProduct);

        // When
        Optional<Product> found = productRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getName(), found.get().getName());
    }

    @Test
    void testFindByCategory() {
        // Given
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("50.00"));
        product1.setCategory("Electronics");
        entityManager.persistAndFlush(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("75.00"));
        product2.setCategory("Electronics");
        entityManager.persistAndFlush(product2);

        Product product3 = new Product();
        product3.setName("Product 3");
        product3.setPrice(new BigDecimal("100.00"));
        product3.setCategory("Books");
        entityManager.persistAndFlush(product3);

        // When
        List<Product> electronics = productRepository.findByCategory("Electronics");

        // Then
        assertEquals(2, electronics.size());
        assertTrue(electronics.stream().allMatch(p -> p.getCategory().equals("Electronics")));
    }

    @Test
    void testFindProductsByCategoryOrderedByPrice() {
        // Given
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("100.00"));
        product1.setCategory("Electronics");
        entityManager.persistAndFlush(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("50.00"));
        product2.setCategory("Electronics");
        entityManager.persistAndFlush(product2);

        // When
        List<Product> products = productRepository.findProductsByCategoryOrderedByPrice("Electronics");

        // Then
        assertEquals(2, products.size());
        assertEquals("Product 2", products.get(0).getName()); // Lower price first
        assertEquals("Product 1", products.get(1).getName());
    }
}

