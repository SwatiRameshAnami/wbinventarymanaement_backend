package com.stockflow.inventory.service.impl;

import com.stockflow.inventory.dto.response.NotificationResponse;
import com.stockflow.inventory.entity.Notification;
import com.stockflow.inventory.enums.NotificationType;
import com.stockflow.inventory.exception.ResourceNotFoundException;
import com.stockflow.inventory.repository.NotificationRepository;
import com.stockflow.inventory.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));
        notification.setRead(true);
        return NotificationResponse.from(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        notificationRepository.markAllAsRead();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        return notificationRepository.countByReadFalse();
    }

    @Override
    @Transactional
    public void createNotification(String message, NotificationType type, Long referenceId) {
        Notification notification = Notification.builder()
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .read(false)
                .build();
        notificationRepository.save(notification);
        log.info("Notification created [{}]: {}", type, message);
    }
}
