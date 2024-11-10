package edu.icet.service;

import edu.icet.dto.Notification;
import edu.icet.entity.NotificationEntity;

import java.util.List;

public interface NotificationService {
    NotificationEntity createNotification(Notification notificationDTO);
    List<NotificationEntity> getNotificationsByEmail(String email);
    NotificationEntity markAsRead(Long id);
    void deleteNotification(Long id);
}
