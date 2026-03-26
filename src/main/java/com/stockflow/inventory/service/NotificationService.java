package com.stockflow.inventory.service;

import com.stockflow.inventory.dto.response.NotificationResponse;
import com.stockflow.inventory.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getAllNotifications();
    NotificationResponse       markAsRead(Long id);
    void                       markAllAsRead();
    long                       getUnreadCount();
    void                       createNotification(String message, NotificationType type, Long referenceId);
}
