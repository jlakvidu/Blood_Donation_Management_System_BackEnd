package edu.icet.service;

import edu.icet.dto.NotificationDTO;
import edu.icet.entity.Notification;

import java.util.List;

public interface NotificationService {
    Notification createNotification(NotificationDTO notificationDTO);
    List<Notification> getNotificationsByEmail(String email);
    Notification markAsRead(Long id);
    void deleteNotification(Long id);
}
