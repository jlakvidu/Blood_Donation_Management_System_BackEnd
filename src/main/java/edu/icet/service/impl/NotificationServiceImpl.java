package edu.icet.service.impl;

import edu.icet.dto.Notification;
import edu.icet.entity.NotificationEntity;
import edu.icet.repository.NotificationDao;
import edu.icet.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationDao notificationDao;

    public NotificationEntity createNotification(Notification notificationDTO) {
        NotificationEntity notification = new NotificationEntity();
        notification.setRecipientEmail(notificationDTO.getRecipientEmail());
        notification.setTitle(notificationDTO.getTitle());
        notification.setMessage(notificationDTO.getMessage());
        notification.setType(notificationDTO.getType());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        return notificationDao.save(notification);
    }

    public List<NotificationEntity> getNotificationsByEmail(String email) {
        return notificationDao.findByRecipientEmailOrderByCreatedAtDesc(email);
    }

    public NotificationEntity markAsRead(Long id) {
        NotificationEntity notification = notificationDao.findById(id)
                .orElseThrow(() -> new RuntimeException("NotificationEntity not found"));

        notification.setRead(true);
        return notificationDao.save(notification);
    }

    public void deleteNotification(Long id) {
        notificationDao.deleteById(id);
    }

    public List<NotificationEntity> getUnreadNotifications(String email) {
        return notificationDao.findByRecipientEmailAndIsReadFalseOrderByCreatedAtDesc(email);
    }

    public long getUnreadCount(String email) {
        return notificationDao.countByRecipientEmailAndIsReadFalse(email);
    }


    public void markAllAsRead(String email) {
        List<NotificationEntity> unreadNotifications = notificationDao
                .findByRecipientEmailAndIsReadFalse(email);

        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationDao.saveAll(unreadNotifications);
    }
}
