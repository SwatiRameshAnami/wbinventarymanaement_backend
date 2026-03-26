package com.stockflow.inventory.service.impl;

import com.stockflow.inventory.dto.request.ProductRequest;
import com.stockflow.inventory.dto.request.StockUpdateRequest;
import com.stockflow.inventory.dto.response.ProductResponse;
import com.stockflow.inventory.entity.Product;
import com.stockflow.inventory.exception.ResourceNotFoundException;
import com.stockflow.inventory.repository.ProductRepository;
import com.stockflow.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(String search, String category, Pageable pageable) {
        return productRepository
                .findAllWithFilters(search, category, pageable)
                .map(ProductResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return ProductResponse.from(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts() {
        return productRepository.findLowStockProducts()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Override
    public long countAll() {
        return productRepository.count();
    }

    @Override
    public long countLowStock() {
        return productRepository.countLowStock();
    }

    // ── Write ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .category(request.getCategory())
                .stockQuantity(request.getStockQuantity())
                .lowStockThreshold(request.getLowStockThreshold())
                .description(request.getDescription())
                .build();

        Product saved = productRepository.save(product);
        log.info("Product created: {} (id={})", saved.getName(), saved.getId());
        return ProductResponse.from(saved);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findById(id);
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setStockQuantity(request.getStockQuantity());
        product.setLowStockThreshold(request.getLowStockThreshold());
        product.setDescription(request.getDescription());

        Product saved = productRepository.save(product);
        log.info("Product updated: {} (id={})", saved.getName(), saved.getId());
        return ProductResponse.from(saved);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
        log.info("Product deleted: {} (id={})", product.getName(), id);
    }

    @Override
    @Transactional
    public ProductResponse updateStock(Long id, StockUpdateRequest request) {
        Product product = findById(id);
        int oldQty = product.getStockQuantity();
        product.setStockQuantity(request.getQuantity());
        Product saved = productRepository.save(product);
        log.info("Stock updated for '{}': {} → {}", product.getName(), oldQty, request.getQuantity());
        return ProductResponse.from(saved);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }
}
