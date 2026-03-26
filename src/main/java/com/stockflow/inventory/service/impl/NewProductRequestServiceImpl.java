package com.stockflow.inventory.service.impl;

import com.stockflow.inventory.dto.request.NewProductRequestDTO;
import com.stockflow.inventory.dto.request.RequestStatusUpdate;
import com.stockflow.inventory.dto.response.NewProductRequestResponse;
import com.stockflow.inventory.entity.NewProductRequest;
import com.stockflow.inventory.entity.User;
import com.stockflow.inventory.enums.NotificationType;
import com.stockflow.inventory.enums.RequestStatus;
import com.stockflow.inventory.exception.BusinessException;
import com.stockflow.inventory.exception.ResourceNotFoundException;
import com.stockflow.inventory.repository.NewProductRequestRepository;
import com.stockflow.inventory.repository.UserRepository;
import com.stockflow.inventory.service.NewProductRequestService;
import com.stockflow.inventory.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewProductRequestServiceImpl implements NewProductRequestService {

    private final NewProductRequestRepository repository;
    private final UserRepository              userRepository;
    private final NotificationService         notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<NewProductRequestResponse> getAllRequests() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(NewProductRequestResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewProductRequestResponse> getMyRequests(String username) {
        User staff = findUser(username);
        return repository.findByStaffOrderByCreatedAtDesc(staff)
                .stream()
                .map(NewProductRequestResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public NewProductRequestResponse createRequest(NewProductRequestDTO dto, String username) {
        User staff = findUser(username);

        NewProductRequest req = NewProductRequest.builder()
                .staff(staff)
                .productName(dto.getName())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .status(RequestStatus.PENDING)
                .build();

        NewProductRequest saved = repository.save(req);

        // Notify admin
        notificationService.createNotification(
                String.format("%s requested new product: %s", staff.getName(), dto.getName()),
                NotificationType.NEW_PRODUCT,
                saved.getId()
        );

        log.info("New product request from {}: {}", username, dto.getName());
        return NewProductRequestResponse.from(saved);
    }

    @Override
    @Transactional
    public NewProductRequestResponse updateStatus(Long id, RequestStatusUpdate update) {
        NewProductRequest req = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NewProductRequest", id));

        if (req.getStatus() != RequestStatus.PENDING) {
            throw new BusinessException("Request already " + req.getStatus().name().toLowerCase());
        }

        req.setStatus(update.getStatus());
        return NewProductRequestResponse.from(repository.save(req));
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
}
