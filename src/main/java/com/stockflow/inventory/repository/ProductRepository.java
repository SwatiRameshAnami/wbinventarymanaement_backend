package com.stockflow.inventory.repository;

import com.stockflow.inventory.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT p FROM Product p
        WHERE (:search IS NULL OR :search = ''
               OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:category IS NULL OR :category = ''
               OR p.category = :category)
        ORDER BY p.createdAt DESC
        """)
    Page<Product> findAllWithFilters(@Param("search")   String search,
                                     @Param("category") String category,
                                     Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.lowStockThreshold ORDER BY p.stockQuantity ASC")
    List<Product> findLowStockProducts();

    

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity <= p.lowStockThreshold")
    long countLowStock();
}
