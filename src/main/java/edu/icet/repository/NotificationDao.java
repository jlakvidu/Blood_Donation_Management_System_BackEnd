package edu.icet.repository;

import edu.icet.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationDao extends JpaRepository<NotificationEntity,Long> {
    List<NotificationEntity> findByRecipientEmailOrderByCreatedAtDesc(String email);
    List<NotificationEntity> findByRecipientEmailAndIsReadFalseOrderByCreatedAtDesc(String email);
    List<NotificationEntity> findByRecipientEmailAndIsReadFalse(String email);
    long countByRecipientEmailAndIsReadFalse(String email);
}
