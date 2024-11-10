package edu.icet.repository;

import edu.icet.entity.EmergencyRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EmergencyRequestDao extends JpaRepository<EmergencyRequestEntity,Long> {
    List<EmergencyRequestEntity> findByStatusOrderByCreatedAtDesc(String status);
    List<EmergencyRequestEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
