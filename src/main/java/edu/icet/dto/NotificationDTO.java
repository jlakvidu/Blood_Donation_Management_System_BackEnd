package edu.icet.dto;

import edu.icet.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificationDTO {
    private String recipientEmail;
    private String title;
    private String message;
    private Notification.NotificationType type;
}
