package edu.icet.service.impl;

import edu.icet.dto.Notification;
import edu.icet.entity.NotificationEntity;
import edu.icet.repository.NotificationDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class NotificationServiceImplTest {

    @MockBean
    private NotificationDao notificationDao;

    @Autowired
    private NotificationServiceImpl notificationService;

    private Notification testNotificationDTO;
    private NotificationEntity testNotificationEntity;

    @BeforeEach
    void setUp() {
        testNotificationDTO = new Notification();
        testNotificationDTO.setRecipientEmail("test@test.com");
        testNotificationDTO.setTitle("Test Notification");
        testNotificationDTO.setMessage("Test Message");
        testNotificationDTO.setType(NotificationEntity.NotificationType.APPOINTMENT_CONFIRMED);

        testNotificationEntity = new NotificationEntity();
        testNotificationEntity.setId(1L);
        testNotificationEntity.setRecipientEmail("test@test.com");
        testNotificationEntity.setTitle("Test Notification");
        testNotificationEntity.setMessage("Test Message");
        testNotificationEntity.setType(NotificationEntity.NotificationType.APPOINTMENT_CONFIRMED);
        testNotificationEntity.setCreatedAt(LocalDateTime.now());
        testNotificationEntity.setRead(false);
    }

    @Test
    void shouldCreateNotification() {
        when(notificationDao.save(any(NotificationEntity.class))).thenReturn(testNotificationEntity);

        NotificationEntity result = notificationService.createNotification(testNotificationDTO);

        assertNotNull(result);
        assertEquals("Test Notification", result.getTitle());
        verify(notificationDao).save(any(NotificationEntity.class));
    }

    @Test
    void shouldGetNotificationsByEmail() {
        when(notificationDao.findByRecipientEmailOrderByCreatedAtDesc("test@test.com"))
                .thenReturn(Arrays.asList(testNotificationEntity));

        List<NotificationEntity> result = notificationService.getNotificationsByEmail("test@test.com");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Notification", result.get(0).getTitle());
    }

    @Test
    void shouldMarkNotificationAsRead() {
        when(notificationDao.findById(1L)).thenReturn(Optional.of(testNotificationEntity));
        when(notificationDao.save(any(NotificationEntity.class))).thenReturn(testNotificationEntity);

        NotificationEntity result = notificationService.markAsRead(1L);

        assertTrue(result.isRead());
        verify(notificationDao).save(any(NotificationEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenMarkingNonExistentNotification() {
        when(notificationDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> notificationService.markAsRead(1L));
    }

    @Test
    void shouldDeleteNotification() {
        notificationService.deleteNotification(1L);

        verify(notificationDao).deleteById(1L);
    }

    @Test
    void shouldGetUnreadNotifications() {
        when(notificationDao.findByRecipientEmailAndIsReadFalseOrderByCreatedAtDesc("test@test.com"))
                .thenReturn(Arrays.asList(testNotificationEntity));

        List<NotificationEntity> result = notificationService.getUnreadNotifications("test@test.com");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertFalse(result.get(0).isRead());
    }

    @Test
    void shouldGetUnreadCount() {
        when(notificationDao.countByRecipientEmailAndIsReadFalse("test@test.com")).thenReturn(5L);

        long result = notificationService.getUnreadCount("test@test.com");

        assertEquals(5L, result);
    }

    @Test
    void shouldMarkAllAsRead() {
        when(notificationDao.findByRecipientEmailAndIsReadFalse("test@test.com"))
                .thenReturn(Arrays.asList(testNotificationEntity));

        notificationService.markAllAsRead("test@test.com");

        verify(notificationDao).saveAll(any());
    }

    @Test
    void shouldHandleEmptyUnreadNotifications() {
        when(notificationDao.findByRecipientEmailAndIsReadFalse("test@test.com"))
                .thenReturn(Arrays.asList());

        notificationService.markAllAsRead("test@test.com");

        verify(notificationDao).saveAll(Arrays.asList());
    }
}