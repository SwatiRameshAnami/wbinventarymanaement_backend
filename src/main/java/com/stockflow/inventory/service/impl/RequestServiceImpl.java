package com.stockflow.inventory.service.impl;

import com.stockflow.inventory.dto.request.CartRequest;
import com.stockflow.inventory.dto.request.RequestStatusUpdate;
import com.stockflow.inventory.dto.response.RequestResponse;
import com.stockflow.inventory.entity.Product;
import com.stockflow.inventory.entity.Request;
import com.stockflow.inventory.entity.User;
import com.stockflow.inventory.enums.NotificationType;
import com.stockflow.inventory.enums.RequestStatus;
import com.stockflow.inventory.exception.BusinessException;
import com.stockflow.inventory.exception.ResourceNotFoundException;
import com.stockflow.inventory.repository.RequestRepository;
import com.stockflow.inventory.repository.UserRepository;
import com.stockflow.inventory.service.NotificationService;
import com.stockflow.inventory.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository    requestRepository;
    private final UserRepository       userRepository;
    private final ProductServiceImpl   productService;
    private final NotificationService  notificationService;

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<RequestResponse> getAllRequests(RequestStatus status, String search, Pageable pageable) {
        return requestRepository.findAllWithFilters(status, search, pageable)
                .map(RequestResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestResponse> getMyRequests(String username) {
        User staff = findUserByUsername(username);
        return requestRepository.findByStaffOrderByRequestDateDesc(staff)
                .stream()
                .map(RequestResponse::from)
                .toList();
    }

    @Override
    public long countPending() {
        return requestRepository.countByStatus(RequestStatus.PENDING);
    }

    // ── Write ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public List<RequestResponse> createCartRequests(CartRequest cartRequest, String username) {
        User staff = findUserByUsername(username);
        List<RequestResponse> responses = new ArrayList<>();

        for (CartRequest.CartItem item : cartRequest.getItems()) {
            Product product = productService.findById(item.getProductId());

            Request request = Request.builder()
                    .staff(staff)
                    .product(product)
                    .quantityRequested(item.getQuantity())
                    .status(RequestStatus.PENDING)
                    .note(cartRequest.getNote())
                    .build();

            Request saved = requestRepository.save(request);
            responses.add(RequestResponse.from(saved));

            // Notify admin
            notificationService.createNotification(
                    String.format("%s requested %d × %s",
                            staff.getName(), item.getQuantity(), product.getName()),
                    NotificationType.REQUEST,
                    saved.getId()
            );

            log.info("Request created: staff={}, product={}, qty={}",
                    staff.getUsername(), product.getName(), item.getQuantity());
        }

        return responses;
    }

    @Override
    @Transactional
    public RequestResponse updateRequestStatus(Long id, RequestStatusUpdate update) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request", id));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BusinessException(
                    "Request has already been " + request.getStatus().name().toLowerCase());
        }

        request.setStatus(update.getStatus());
        if (update.getNote() != null && !update.getNote().isBlank()) {
            request.setNote(update.getNote());
        }

        Request saved = requestRepository.save(request);
        log.info("Request {} → {}", id, update.getStatus());
        return RequestResponse.from(saved);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
}
