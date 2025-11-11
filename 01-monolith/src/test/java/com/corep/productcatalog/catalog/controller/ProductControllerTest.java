package com.corep.productcatalog.catalog.controller;

import com.corep.productcatalog.catalog.dto.ProductDTO;
import com.corep.productcatalog.catalog.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateProduct() throws Exception {
        // Given
        ProductDTO inputDTO = new ProductDTO();
        inputDTO.setName("Test Product");
        inputDTO.setDescription("Test Description");
        inputDTO.setPrice(new BigDecimal("99.99"));
        inputDTO.setCategory("Electronics");

        ProductDTO outputDTO = new ProductDTO();
        outputDTO.setId(1L);
        outputDTO.setName("Test Product");
        outputDTO.setDescription("Test Description");
        outputDTO.setPrice(new BigDecimal("99.99"));
        outputDTO.setCategory("Electronics");

        when(productService.createProduct(any(ProductDTO.class))).thenReturn(outputDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    void testGetAllProducts() throws Exception {
        // Given
        ProductDTO product1 = new ProductDTO();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("99.99"));

        ProductDTO product2 = new ProductDTO();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("149.99"));

        List<ProductDTO> products = Arrays.asList(product1, product2);
        when(productService.getAllProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void testGetProductById() throws Exception {
        // Given
        ProductDTO product = new ProductDTO();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("99.99"));

        when(productService.getProductById(1L)).thenReturn(product);

        // When & Then
        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        // Given
        ProductDTO inputDTO = new ProductDTO();
        inputDTO.setName("Updated Product");
        inputDTO.setPrice(new BigDecimal("149.99"));
        inputDTO.setCategory("Electronics");

        ProductDTO outputDTO = new ProductDTO();
        outputDTO.setId(1L);
        outputDTO.setName("Updated Product");
        outputDTO.setPrice(new BigDecimal("149.99"));
        outputDTO.setCategory("Electronics");

        when(productService.updateProduct(eq(1L), any(ProductDTO.class))).thenReturn(outputDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Product"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCreateProduct_ValidationError() throws Exception {
        // Given - ProductDTO with missing required fields
        ProductDTO invalidDTO = new ProductDTO();
        invalidDTO.setName(""); // Empty name should fail validation

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }
}

