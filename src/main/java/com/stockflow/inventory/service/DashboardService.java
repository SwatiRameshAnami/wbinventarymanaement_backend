package com.stockflow.inventory.service;

import com.stockflow.inventory.dto.response.AdminDashboardStats;

public interface DashboardService {
    AdminDashboardStats getAdminStats();
}
