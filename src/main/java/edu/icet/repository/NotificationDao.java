package edu.icet.repository;

import edu.icet.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationDao extends JpaRepository<Notification,Long> {
    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String email);
    List<Notification> findByRecipientEmailAndIsReadFalseOrderByCreatedAtDesc(String email);
    List<Notification> findByRecipientEmailAndIsReadFalse(String email);
    long countByRecipientEmailAndIsReadFalse(String email);
}
