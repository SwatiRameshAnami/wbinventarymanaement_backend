package com.stockflow.inventory.controller;

import com.stockflow.inventory.dto.request.CartRequest;
import com.stockflow.inventory.dto.request.RequestStatusUpdate;
import com.stockflow.inventory.dto.response.ApiResponse;
import com.stockflow.inventory.dto.response.RequestResponse;
import com.stockflow.inventory.enums.RequestStatus;
import com.stockflow.inventory.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    // ── GET /api/requests  (Admin: all requests with filters) ─────────────────
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<RequestResponse>>> getAllRequests(
            @RequestParam(required = false)           String status,
            @RequestParam(required = false)           String search,
            @RequestParam(defaultValue = "0")         int    page,
            @RequestParam(defaultValue = "10")        int    size) {

        RequestStatus statusEnum = null;
        if (status != null && !status.isBlank()) {
            statusEnum = RequestStatus.valueOf(status.toUpperCase());
        }

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("requestDate").descending());

        Page<RequestResponse> requests =
                requestService.getAllRequests(statusEnum, search, pageable);

        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    // ── GET /api/requests/my  (Staff: own requests) ───────────────────────────
    @GetMapping("/my")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<List<RequestResponse>>> getMyRequests(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<RequestResponse> requests =
                requestService.getMyRequests(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    // ── POST /api/requests  (Staff: submit cart) ──────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<List<RequestResponse>>> createRequests(
            @Valid @RequestBody CartRequest cartRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<RequestResponse> responses =
                requestService.createCartRequests(cartRequest, userDetails.getUsername());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Request submitted successfully", responses));
    }

    // ── PATCH /api/requests/{id}/status  (Admin: approve / reject) ───────────
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RequestResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody RequestStatusUpdate update) {

        RequestResponse response = requestService.updateRequestStatus(id, update);
        return ResponseEntity.ok(
                ApiResponse.success("Request " + update.getStatus().name().toLowerCase(), response));
    }
}
