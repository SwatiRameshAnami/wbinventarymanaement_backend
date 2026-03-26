package com.stockflow.inventory.controller;

import com.stockflow.inventory.dto.request.PurchaseRequest;
import com.stockflow.inventory.dto.response.ApiResponse;
import com.stockflow.inventory.dto.response.PurchaseResponse;
import com.stockflow.inventory.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PurchaseController {

    private final PurchaseService purchaseService;

    // ── GET /api/purchases  (paginated list) ──────────────────────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PurchaseResponse>>> getAllPurchases(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PurchaseResponse> purchases = purchaseService.getAllPurchases(pageable);
        return ResponseEntity.ok(ApiResponse.success(purchases));
    }

    // ── GET /api/purchases/report  (for Reports page) ────────────────────────
    // Query params: range=weekly|monthly|custom, from=YYYY-MM-DD, to=YYYY-MM-DD
    @GetMapping("/report")
    public ResponseEntity<ApiResponse<List<PurchaseResponse>>> getReport(
            @RequestParam(required = false, defaultValue = "monthly") String range,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<PurchaseResponse> report = purchaseService.getReport(range, from, to);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    // ── POST /api/purchases  (record a purchase, auto-increments stock) ───────
    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseResponse>> createPurchase(
            @Valid @RequestBody PurchaseRequest request) {

        PurchaseResponse purchase = purchaseService.createPurchase(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Purchase recorded and stock updated", purchase));
    }
}
