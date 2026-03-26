package com.stockflow.inventory.service;

import com.stockflow.inventory.dto.request.CartRequest;
import com.stockflow.inventory.dto.request.RequestStatusUpdate;
import com.stockflow.inventory.dto.response.RequestResponse;
import com.stockflow.inventory.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RequestService {
    Page<RequestResponse> getAllRequests(RequestStatus status, String search, Pageable pageable);
    List<RequestResponse> getMyRequests(String username);
    List<RequestResponse> createCartRequests(CartRequest cartRequest, String username);
    RequestResponse       updateRequestStatus(Long id, RequestStatusUpdate update);
    long                  countPending();
}
