package com.corep.productcatalog.catalog.service;

import com.corep.productcatalog.catalog.dto.ProductDTO;
import com.corep.productcatalog.catalog.entity.Product;
import com.corep.productcatalog.catalog.exception.ProductNotFoundException;
import com.corep.productcatalog.catalog.repository.ProductRepository;
import com.corep.productcatalog.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductDTO testProductDTO;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setCategory("Electronics");
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());

        testProductDTO = new ProductDTO();
        testProductDTO.setName("Test Product");
        testProductDTO.setDescription("Test Description");
        testProductDTO.setPrice(new BigDecimal("99.99"));
        testProductDTO.setCategory("Electronics");
    }

    @Test
    void testCreateProduct() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        // Mock InventoryService.createInventory to return a dummy InventoryDTO
        // (we don't care about the return value, just that it's called)
        when(inventoryService.createInventory(eq(1L), eq(0))).thenReturn(null);

        // When
        ProductDTO result = productService.createProduct(testProductDTO);

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getName(), result.getName());
        assertEquals(testProduct.getPrice(), result.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(inventoryService, times(1)).createInventory(eq(1L), eq(0));
    }

    @Test
    void testGetProductById_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        ProductDTO result = productService.getProductById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getName(), result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(999L);
        });
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdateProduct_Success() {
        // Given
        ProductDTO updateDTO = new ProductDTO();
        updateDTO.setName("Updated Product");
        updateDTO.setDescription("Updated Description");
        updateDTO.setPrice(new BigDecimal("149.99"));
        updateDTO.setCategory("Electronics");

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        ProductDTO result = productService.updateProduct(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        // Given
        ProductDTO updateDTO = new ProductDTO();
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct(999L, updateDTO);
        });
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(inventoryService).deleteInventory(1L);
        doNothing().when(productRepository).deleteById(1L);

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository, times(1)).existsById(1L);
        verify(inventoryService, times(1)).deleteInventory(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProduct_NotFound() {
        // Given
        when(productRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct(999L);
        });
        verify(productRepository, times(1)).existsById(999L);
        verify(inventoryService, never()).deleteInventory(anyLong());
        verify(productRepository, never()).deleteById(anyLong());
    }
}

