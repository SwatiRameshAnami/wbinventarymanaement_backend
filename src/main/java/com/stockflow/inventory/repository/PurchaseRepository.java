package com.stockflow.inventory.repository;

import com.stockflow.inventory.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Page<Purchase> findAllByOrderByPurchaseDateDesc(Pageable pageable);

    @Query("""
        SELECT p FROM Purchase p
        JOIN FETCH p.product
        WHERE p.purchaseDate BETWEEN :from AND :to
        ORDER BY p.purchaseDate DESC
        """)
    List<Purchase> findByDateRange(@Param("from") LocalDate from,
                                   @Param("to")   LocalDate to);

    @Query("SELECT COALESCE(SUM(p.totalCost), 0) FROM Purchase p WHERE p.purchaseDate BETWEEN :from AND :to")
    BigDecimal sumTotalCostByDateRange(@Param("from") LocalDate from,
                                       @Param("to")   LocalDate to);

    @Query("SELECT p FROM Purchase p JOIN FETCH p.product ORDER BY p.purchaseDate DESC")
    List<Purchase> findAllWithProduct();
}
