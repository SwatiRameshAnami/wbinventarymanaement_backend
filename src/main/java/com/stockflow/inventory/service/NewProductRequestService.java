package com.stockflow.inventory.service;

import com.stockflow.inventory.dto.request.NewProductRequestDTO;
import com.stockflow.inventory.dto.request.RequestStatusUpdate;
import com.stockflow.inventory.dto.response.NewProductRequestResponse;

import java.util.List;

public interface NewProductRequestService {
    List<NewProductRequestResponse> getAllRequests();
    List<NewProductRequestResponse> getMyRequests(String username);
    NewProductRequestResponse       createRequest(NewProductRequestDTO dto, String username);
    NewProductRequestResponse       updateStatus(Long id, RequestStatusUpdate update);
}
