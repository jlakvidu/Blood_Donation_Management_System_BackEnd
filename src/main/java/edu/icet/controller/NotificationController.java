package edu.icet.controller;

import edu.icet.dto.Notification;
import edu.icet.entity.NotificationEntity;
import edu.icet.service.EmailService;
import edu.icet.service.impl.EmailServiceImpl;
import edu.icet.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@CrossOrigin
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    private final EmailService emailService;

    @PostMapping("/create")
    public ResponseEntity<String> createNotification(@RequestBody Notification notification) {
        try {
            emailService.sendAppointmentStatusEmail(notification);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("NotificationEntity created and email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Error processing notification: " + e.getMessage());
        }
    }


    @GetMapping("/user/{email}")
    public ResponseEntity<List<NotificationEntity>> getUserNotifications(@PathVariable String email) {
        try {
            List<NotificationEntity> notifications = notificationService.getNotificationsByEmail(email);
            System.out.println("Fetching notifications for email: " + email);
            System.out.println("Found notifications: " + notifications.size());
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.err.println("Error fetching notifications: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok().body("NotificationEntity deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting notification: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationEntity> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }
}
