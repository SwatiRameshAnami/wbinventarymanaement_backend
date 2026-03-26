package com.stockflow.inventory.service;

import com.stockflow.inventory.dto.request.PurchaseRequest;
import com.stockflow.inventory.dto.response.PurchaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PurchaseService {
    Page<PurchaseResponse> getAllPurchases(Pageable pageable);
    PurchaseResponse       createPurchase(PurchaseRequest request);
    List<PurchaseResponse> getReport(String range, LocalDate from, LocalDate to);
    BigDecimal             getTotalCostThisMonth();
}
