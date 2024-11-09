package edu.icet.service;

import edu.icet.dto.NotificationDTO;
import edu.icet.entity.Notification;
import edu.icet.repository.NotificationDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationDao notificationDao;

    public Notification createNotification(NotificationDTO notificationDTO) {
        Notification notification = new Notification();
        notification.setRecipientEmail(notificationDTO.getRecipientEmail());
        notification.setTitle(notificationDTO.getTitle());
        notification.setMessage(notificationDTO.getMessage());
        notification.setType(notificationDTO.getType());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        return notificationDao.save(notification);
    }

    public List<Notification> getNotificationsByEmail(String email) {
        return notificationDao.findByRecipientEmailOrderByCreatedAtDesc(email);
    }

    public Notification markAsRead(Long id) {
        Notification notification = notificationDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        return notificationDao.save(notification);
    }

    public void deleteNotification(Long id) {
        notificationDao.deleteById(id);
    }

    public List<Notification> getUnreadNotifications(String email) {
        return notificationDao.findByRecipientEmailAndIsReadFalseOrderByCreatedAtDesc(email);
    }

    public long getUnreadCount(String email) {
        return notificationDao.countByRecipientEmailAndIsReadFalse(email);
    }


    public void markAllAsRead(String email) {
        List<Notification> unreadNotifications = notificationDao
                .findByRecipientEmailAndIsReadFalse(email);

        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationDao.saveAll(unreadNotifications);
    }
}
