package edu.icet.dto;

import edu.icet.entity.NotificationEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Notification {
    private String recipientEmail;
    private String title;
    private String message;
    private NotificationEntity.NotificationType type;
}
