package com.stockflow.inventory.service.impl;

import com.stockflow.inventory.dto.response.AdminDashboardStats;
import com.stockflow.inventory.repository.UserRepository;
import com.stockflow.inventory.service.DashboardService;
import com.stockflow.inventory.service.ProductService;
import com.stockflow.inventory.service.PurchaseService;
import com.stockflow.inventory.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProductService  productService;
    private final RequestService  requestService;
    private final PurchaseService purchaseService;
    private final UserRepository  userRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardStats getAdminStats() {
        return AdminDashboardStats.builder()
                .totalProducts(productService.countAll())
                .lowStockProducts(productService.countLowStock())
                .pendingRequests(requestService.countPending())
                .totalPurchasesThisMonth(purchaseService.getTotalCostThisMonth())
                .totalUsers(userRepository.count())
                .build();
    }
}
