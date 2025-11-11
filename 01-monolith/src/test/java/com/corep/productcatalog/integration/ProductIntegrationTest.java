package com.corep.productcatalog.integration;

import com.corep.productcatalog.catalog.dto.ProductDTO;
import com.corep.productcatalog.catalog.entity.Product;
import com.corep.productcatalog.catalog.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full integration test
 * Tests the entire application stack (Controller -> Service -> Repository -> Database)
 * Uses @SpringBootTest to load the full application context
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        productRepository.deleteAll();

        // Create test product
        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setCategory("Electronics");
        testProduct = productRepository.save(testProduct);
    }

    @Test
    void testCreateProduct_Integration() throws Exception {
        ProductDTO newProduct = new ProductDTO();
        newProduct.setName("New Product");
        newProduct.setDescription("New Description");
        newProduct.setPrice(new BigDecimal("149.99"));
        newProduct.setCategory("Books");

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.price").value(149.99));

        // Verify in database
        assert productRepository.count() == 2;
    }

    @Test
    void testGetProductById_Integration() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}", testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProduct.getId()))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void testUpdateProduct_Integration() throws Exception {
        ProductDTO updateDTO = new ProductDTO();
        updateDTO.setName("Updated Product");
        updateDTO.setPrice(new BigDecimal("199.99"));
        updateDTO.setCategory("Electronics");

        mockMvc.perform(put("/api/v1/products/{id}", testProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(199.99));

        // Verify update in database
        Product updated = productRepository.findById(testProduct.getId()).orElseThrow();
        assert updated.getName().equals("Updated Product");
    }

    @Test
    void testDeleteProduct_Integration() throws Exception {
        mockMvc.perform(delete("/api/v1/products/{id}", testProduct.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion in database
        assert productRepository.count() == 0;
        assert productRepository.findById(testProduct.getId()).isEmpty();
    }

    @Test
    void testGetAllProducts_Integration() throws Exception {
        // Create another product
        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("50.00"));
        product2.setCategory("Books");
        productRepository.save(product2);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void testGetProductsByCategory_Integration() throws Exception {
        // Create product in different category
        Product product2 = new Product();
        product2.setName("Book Product");
        product2.setPrice(new BigDecimal("30.00"));
        product2.setCategory("Books");
        productRepository.save(product2);

        mockMvc.perform(get("/api/v1/products")
                        .param("category", "Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].category").value("Electronics"));
    }
}
