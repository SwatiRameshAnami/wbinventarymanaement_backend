package com.stockflow.inventory.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AdminDashboardStats {
    private long totalProducts;
    private long lowStockProducts;
    private long pendingRequests;
    private BigDecimal totalPurchasesThisMonth;
    private long totalUsers;
}
