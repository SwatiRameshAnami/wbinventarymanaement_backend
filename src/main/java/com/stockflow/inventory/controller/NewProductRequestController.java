package com.stockflow.inventory.controller;

import com.stockflow.inventory.dto.request.NewProductRequestDTO;
import com.stockflow.inventory.dto.request.RequestStatusUpdate;
import com.stockflow.inventory.dto.response.ApiResponse;
import com.stockflow.inventory.dto.response.NewProductRequestResponse;
import com.stockflow.inventory.service.NewProductRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/new-product-requests")
@RequiredArgsConstructor
public class NewProductRequestController {

    private final NewProductRequestService newProductRequestService;

    // ── GET /api/new-product-requests/all  (Admin only) ──────────────────────
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<NewProductRequestResponse>>> getAllRequests() {
        return ResponseEntity.ok(
                ApiResponse.success(newProductRequestService.getAllRequests()));
    }

    // ── GET /api/new-product-requests  (Staff: own requests) ─────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<List<NewProductRequestResponse>>> getMyRequests(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<NewProductRequestResponse> list =
                newProductRequestService.getMyRequests(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    // ── POST /api/new-product-requests  (Staff: submit new product request) ───
    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<NewProductRequestResponse>> createRequest(
            @Valid @RequestBody NewProductRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        NewProductRequestResponse response =
                newProductRequestService.createRequest(dto, userDetails.getUsername());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("New product request submitted", response));
    }

    // ── PATCH /api/new-product-requests/{id}/status  (Admin: approve/reject) ─
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NewProductRequestResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody RequestStatusUpdate update) {

        NewProductRequestResponse response =
                newProductRequestService.updateStatus(id, update);
        return ResponseEntity.ok(
                ApiResponse.success("Status updated to " + update.getStatus(), response));
    }
}
