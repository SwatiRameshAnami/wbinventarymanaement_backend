package com.stockflow.inventory.service;

import com.stockflow.inventory.dto.request.ProductRequest;
import com.stockflow.inventory.dto.request.StockUpdateRequest;
import com.stockflow.inventory.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Page<ProductResponse> getAllProducts(String search, String category, Pageable pageable);
    ProductResponse       getProductById(Long id);
    ProductResponse       createProduct(ProductRequest request);
    ProductResponse       updateProduct(Long id, ProductRequest request);
    void                  deleteProduct(Long id);
    ProductResponse       updateStock(Long id, StockUpdateRequest request);
    List<ProductResponse> getLowStockProducts();
    long                  countAll();
    long                  countLowStock();
}
