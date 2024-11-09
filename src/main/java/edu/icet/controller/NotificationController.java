package edu.icet.controller;

import edu.icet.dto.NotificationDTO;
import edu.icet.entity.Notification;
import edu.icet.service.EmailService;
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
    public ResponseEntity<String> createNotification(@RequestBody NotificationDTO notification) {
        try {
            emailService.sendAppointmentStatusEmail(notification);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Notification created and email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Error processing notification: " + e.getMessage());
        }
    }


    @GetMapping("/user/{email}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String email) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByEmail(email);
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
            return ResponseEntity.ok().body("Notification deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting notification: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }
}
