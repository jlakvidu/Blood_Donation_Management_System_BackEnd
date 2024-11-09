package edu.icet.repository;

import edu.icet.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CampaignDao extends JpaRepository<Campaign,Long> {
    List<Campaign> findAllByOrderByCreatedAtDesc();
    List<Campaign> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
