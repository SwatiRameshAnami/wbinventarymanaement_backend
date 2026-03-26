package com.stockflow.inventory.service.impl;

import com.stockflow.inventory.dto.request.PurchaseRequest;
import com.stockflow.inventory.dto.response.PurchaseResponse;
import com.stockflow.inventory.entity.Product;
import com.stockflow.inventory.entity.Purchase;
import com.stockflow.inventory.exception.ResourceNotFoundException;
import com.stockflow.inventory.repository.PurchaseRepository;
import com.stockflow.inventory.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductServiceImpl productService;

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseResponse> getAllPurchases(Pageable pageable) {
        return purchaseRepository.findAllByOrderByPurchaseDateDesc(pageable)
                .map(PurchaseResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseResponse> getReport(String range, LocalDate from, LocalDate to) {
        LocalDate[] dates = resolveDateRange(range, from, to);
        return purchaseRepository.findByDateRange(dates[0], dates[1])
                .stream()
                .map(PurchaseResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCostThisMonth() {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end   = LocalDate.now();
        return purchaseRepository.sumTotalCostByDateRange(start, end);
    }

    // ── Write ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request) {
        Product product = productService.findById(request.getProductId());

        BigDecimal total = request.getPricePerUnit()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        Purchase purchase = Purchase.builder()
                .product(product)
                .quantity(request.getQuantity())
                .pricePerUnit(request.getPricePerUnit())
                .totalCost(total)
                .purchaseDate(request.getPurchaseDate())
                .build();

        Purchase saved = purchaseRepository.save(purchase);

        // Auto-increment stock
        int newStock = product.getStockQuantity() + request.getQuantity();
        product.setStockQuantity(newStock);
        // productService already has a reference to productRepository via composition
        // We simply update via the entity (already managed in this transaction)
        log.info("Purchase recorded: {} × {} @ {} = {} | New stock: {}",
                request.getQuantity(), product.getName(),
                request.getPricePerUnit(), total, newStock);

        return PurchaseResponse.from(saved);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private LocalDate[] resolveDateRange(String range, LocalDate from, LocalDate to) {
        return switch (range == null ? "monthly" : range.toLowerCase()) {
            case "weekly"  -> new LocalDate[]{ LocalDate.now().minusWeeks(1),  LocalDate.now() };
            case "custom"  -> new LocalDate[]{
                    from != null ? from : LocalDate.now().minusMonths(1),
                    to   != null ? to   : LocalDate.now()
            };
            default        -> new LocalDate[]{ LocalDate.now().withDayOfMonth(1), LocalDate.now() };
        };
    }
}
