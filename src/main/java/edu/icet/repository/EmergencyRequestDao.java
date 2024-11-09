package edu.icet.repository;

import edu.icet.entity.EmergencyRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EmergencyRequestDao extends JpaRepository<EmergencyRequest,Long> {
    List<EmergencyRequest> findByStatusOrderByCreatedAtDesc(String status);
    List<EmergencyRequest> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
