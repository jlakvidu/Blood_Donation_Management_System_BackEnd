package edu.icet.repository;

import edu.icet.entity.CampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CampaignDao extends JpaRepository<CampaignEntity,Long> {
    List<CampaignEntity> findAllByOrderByCreatedAtDesc();
    List<CampaignEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
