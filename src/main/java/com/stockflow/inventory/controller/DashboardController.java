package com.stockflow.inventory.controller;

import com.stockflow.inventory.dto.response.AdminDashboardStats;
import com.stockflow.inventory.dto.response.ApiResponse;
import com.stockflow.inventory.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // ── GET /api/dashboard/admin ──────────────────────────────────────────────
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminDashboardStats>> getAdminStats() {
        AdminDashboardStats stats = dashboardService.getAdminStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    // ── GET /api/dashboard/staff ──────────────────────────────────────────────
    @GetMapping("/staff")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<Void>> getStaffDashboard() {
        // Staff dashboard is assembled on the frontend from other endpoints.
        // This endpoint exists for parity / future extension.
        return ResponseEntity.ok(ApiResponse.success("Staff dashboard", null));
    }
}
